package edu.java.repository.jooq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.domain.pojos.Links;
import edu.java.domain.pojos.Subscriptions;
import edu.java.domain.pojos.Tgchats;
import edu.java.exceptions.AlreadyTrackLinkException;
import edu.java.exceptions.NotExistLinkException;
import edu.java.exceptions.NotExistTgChatException;
import edu.java.exceptions.NotTrackLinkException;
import edu.java.processors.UriProcessor;
import edu.java.repository.LinkDao;
import edu.java.repository.LinkRepository;
import edu.java.repository.TgChatRepository;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jooq.DSLContext;
import reactor.core.publisher.Mono;
import static edu.java.domain.jooq.tables.Links.LINKS;
import static edu.java.domain.jooq.tables.Subscriptions.SUBSCRIPTIONS;
import static edu.java.domain.jooq.tables.Tgchats.TGCHATS;

@RequiredArgsConstructor
public class JooqLinkDao implements LinkDao {
    private final LinkRepository linkRepository;
    private final TgChatRepository tgChatRepository;
    private final DSLContext dslContext;
    private final ObjectMapper objectMapper;
    private final UriProcessor uriProcessor;

    @SneakyThrows
    @Override
    public Mono<Links> add(Long chatId, URI uri) {
        return getChatIfExist(chatId).map(Tgchats::getId)
            .flatMap(id -> getLinkIfExist(uri).onErrorResume(throwable -> createLink(uri))
            ).flatMap(link -> {
                    var linkId = link.getId();
                    try {
                        dslContext
                            .insertInto(SUBSCRIPTIONS)
                            .columns(SUBSCRIPTIONS.CHAT_ID, SUBSCRIPTIONS.LINK_ID, SUBSCRIPTIONS.CREATED_AT)
                            .values(chatId, linkId, OffsetDateTime.now())
                            .execute();

                        return Mono.just(new Links(
                            linkId,
                            link.getLinkUri(),
                            link.getCreatedAt(),
                            link.getCreatedBy(),
                            link.getContent(),
                            link.getLastModifying()
                        ));
                    } catch (Exception e) {
                        return Mono.error(new AlreadyTrackLinkException());
                    }
                }
            );
    }

    @Override
    public Mono<Links> remove(Long chatId, URI uri) {
        return getChatIfExist(chatId)
            .flatMap(chat -> getLinkIfExist(uri))
            .flatMap(link -> {
                var linkId = link.getId();
                dslContext
                    .delete(SUBSCRIPTIONS)
                    .where(SUBSCRIPTIONS.LINK_ID.eq(getRelationIfExist(chatId, linkId).getLinkId()))
                    .execute();
                return Mono.just(link);
            }).map(link -> {
                deleteUntraceableLinks(link.getId());
                return link;
            });
    }

    @Override
    public Mono<List<Links>> getAllLinkInRelation(Long chatId) {
        return getChatIfExist(chatId).map(chat -> dslContext
            .selectFrom(LINKS)
            .where(
                LINKS.ID.in(
                    dslContext
                        .select(SUBSCRIPTIONS.LINK_ID)
                        .from(SUBSCRIPTIONS)
                        .where(SUBSCRIPTIONS.CHAT_ID.eq(chat.getId()))
                )
            )
            .fetchInto(Links.class));
    }

    @Override
    public Mono<List<Long>> findAllIdTgChatWhoTrackLink(Long uriId) {
        return Mono.just(dslContext
            .selectFrom(TGCHATS)
            .where(
                TGCHATS.ID.in(
                    dslContext
                        .select(SUBSCRIPTIONS.CHAT_ID)
                        .from(SUBSCRIPTIONS)
                        .where(SUBSCRIPTIONS.LINK_ID.eq(uriId))
                )
            ).fetchInto(Tgchats.class).stream().map(Tgchats::getId).toList());
    }


   /*
    Utility methods
     */

    /**
     * Method of searching for a TgChat entity by id.
     *
     * @param id entity id.
     * @return TgChat by id if it exists, otherwise throws a NotExistTgChatException.
     */
    private Mono<Tgchats> getChatIfExist(Long id) {
        return tgChatRepository.findById(id).flatMap(tgChat ->
            tgChat.map(Mono::just).orElse(Mono.error(new NotExistTgChatException())));
    }

    /**
     * Method of searching for a Link entity by link name.
     *
     * @param uri link name.
     * @return Link by id if it exists, otherwise throws a NotExistLinkException.
     */
    private Mono<Links> getLinkIfExist(URI uri) {
        return linkRepository.findLinkByName(uri).flatMap(link ->
            link.map(Mono::just).orElse(Mono.error(new NotExistLinkException())));
    }

    /**
     * Method of searching for a Relation entity.
     *
     * @param chatId chat id.
     * @param uriId  link id.
     * @return Relation by id if it exists, otherwise throws a NotTrackLinkException.
     */
    private Subscriptions getRelationIfExist(Long chatId, Long uriId) {
        return findRelationBetweenTgChatAndLink(chatId, uriId).orElseThrow(NotTrackLinkException::new);
    }

    private Mono<Boolean> isLinkTrack(Long id) {
        return findAllIdTgChatWhoTrackLink(id).map(List::isEmpty);
    }

    /*
    Auxiliary database queries.
     */

    /**
     * Method finds a row in the relation table that shows that the chat is tracking the link.
     *
     * @param chatId chat id.
     * @param uriId  link id.
     * @return empty Optional if there is no relation in the database, otherwise one Relation object.
     */
    private Optional<Subscriptions> findRelationBetweenTgChatAndLink(Long chatId, Long uriId) {
        var resultRelations = dslContext
            .selectFrom(SUBSCRIPTIONS)
            .where(SUBSCRIPTIONS.CHAT_ID.eq(chatId).and(SUBSCRIPTIONS.LINK_ID.eq(uriId)))
            .fetchInto(Subscriptions.class);

        return resultRelations.isEmpty() ? Optional.empty() : Optional.of(resultRelations.getFirst());
    }

    /**
     * Method deletes the link entity if no one else is tracking it.
     *
     * @param linkId link ID.
     */
    private void deleteUntraceableLinks(Long linkId) {
        isLinkTrack(linkId).subscribe(logical -> {
            if (logical) {
                dslContext
                    .delete(LINKS)
                    .where(LINKS.ID.eq(linkId))
                    .execute();
            }
        });
    }

    /**
     * Method for creating a link entity in the links table.
     * Method gets the content at the start of tracking,
     * which will be stored in json, then saves the link with the content in the database.
     *
     * @return the Link object, which contains the link name and the identifier assigned to it in the database.
     */
    @SneakyThrows
    private Mono<Links> createLink(URI linkUri) {
        return Mono.defer(() -> {
            try {
                var content = objectMapper.writeValueAsString(uriProcessor.processUri(linkUri));
                dslContext
                    .insertInto(LINKS)
                    .columns(LINKS.LINK_URI, LINKS.CONTENT, LINKS.CREATED_AT)
                    .values(linkUri.toString(), content, OffsetDateTime.now())
                    .execute();
                return getLinkIfExist(linkUri);
            } catch (JsonProcessingException e) {
                return Mono.error(e);
            }
        });
    }
}
