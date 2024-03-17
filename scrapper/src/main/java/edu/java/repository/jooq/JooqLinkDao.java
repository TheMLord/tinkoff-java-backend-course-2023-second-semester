package edu.java.repository.jooq;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.domain.jooq.tables.pojos.Link;
import edu.java.domain.jooq.tables.pojos.Relation;
import edu.java.domain.jooq.tables.pojos.Tgchat;
import edu.java.exceptions.AlreadyTrackLinkException;
import edu.java.exceptions.NotExistLinkException;
import edu.java.exceptions.NotExistTgChatException;
import edu.java.exceptions.NotTrackLinkException;
import edu.java.processors.UriProcessor;
import edu.java.repository.LinkDao;
import edu.java.repository.LinkRepository;
import edu.java.repository.TgChatRepository;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jooq.DSLContext;
import static edu.java.domain.jooq.tables.Link.LINK;
import static edu.java.domain.jooq.tables.Relation.RELATION;
import static edu.java.domain.jooq.tables.Tgchat.TGCHAT;

@RequiredArgsConstructor
public class JooqLinkDao implements LinkDao {
    private final LinkRepository linkRepository;
    private final TgChatRepository tgChatRepository;
    private final DSLContext dslContext;
    private final ObjectMapper objectMapper;
    private final UriProcessor uriProcessor;

    @SneakyThrows
    @Override
    public Link add(Long chatId, URI uri) {
        var tgChatId = getChatIfExist(chatId).getChatId();

        Link link;
        try {
            link = getLinkIfExist(uri);
        } catch (NotExistLinkException e) {
            link = createLink(uri);
        }
        var linkId = link.getId();
        /*
        The error is generated by the check_duplicate trigger.
        */
        try {
            dslContext
                .insertInto(RELATION)
                .columns(RELATION.CHAT_ID, RELATION.LINK_ID)
                .values(tgChatId, linkId)
                .execute();

            return new Link(
                linkId,
                link.getLinkName(),
                link.getCreatedAt(),
                link.getCreatedBy(),
                link.getContent(),
                link.getLastModifying()
            );
        } catch (Exception e) {
            throw new AlreadyTrackLinkException();
        }
    }

    @Override
    public Link remove(Long chatId, URI uri) {
        var tgChatId = getChatIfExist(chatId).getChatId();
        var link = getLinkIfExist(uri);

        var linkId = link.getId();
        var relationId = getRelationIfExist(tgChatId, linkId).getId();

        dslContext
            .delete(RELATION)
            .where(RELATION.ID.eq(relationId))
            .execute();
        deleteUntraceableLinks(linkId);

        return new Link(
            linkId,
            link.getLinkName(),
            link.getCreatedAt(),
            link.getCreatedBy(),
            link.getContent(),
            link.getLastModifying()
        );
    }

    @Override
    public List<Link> getAllLinkInRelation(Long chatId) {
        var tgChat = getChatIfExist(chatId);
        return dslContext
            .selectFrom(LINK)
            .where(
                LINK.ID.in(
                    dslContext
                        .select(RELATION.LINK_ID)
                        .from(RELATION)
                        .where(RELATION.CHAT_ID.eq(tgChat.getChatId()))
                )
            )
            .fetch()
            .into(Link.class);
    }

    @Override
    public List<Long> findAllIdTgChatWhoTrackLink(Long uriId) {
        return dslContext
            .selectFrom(TGCHAT)
            .where(
                TGCHAT.CHAT_ID.in(
                    dslContext
                        .select(RELATION.CHAT_ID)
                        .from(RELATION)
                        .where(RELATION.LINK_ID.eq(uriId))
                )
            ) .fetch()
            .into(Long.class);
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
    private Tgchat getChatIfExist(Long id) {
        return tgChatRepository.findById(id).orElseThrow(NotExistTgChatException::new);
    }

    /**
     * Method of searching for a Link entity by link name.
     *
     * @param uri link name.
     * @return Link by id if it exists, otherwise throws a NotExistLinkException.
     */
    private Link getLinkIfExist(URI uri) {
        return linkRepository.findLinkByName(uri).orElseThrow(NotExistLinkException::new);
    }

    /**
     * Method of searching for a Relation entity.
     *
     * @param chatId chat id.
     * @param uriId  link id.
     * @return Relation by id if it exists, otherwise throws a NotTrackLinkException.
     */
    private Relation getRelationIfExist(Long chatId, Long uriId) {
        return findRelationBetweenTgChatAndLink(chatId, uriId).orElseThrow(NotTrackLinkException::new);
    }

    private boolean isLinkTrack(Long id) {
        return findAllIdTgChatWhoTrackLink(id).isEmpty();
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
    private Optional<Relation> findRelationBetweenTgChatAndLink(Long chatId, Long uriId) {
        var relations = dslContext
            .selectFrom(RELATION)
            .where(RELATION.CHAT_ID.eq(chatId))
            .and(RELATION.LINK_ID.eq(uriId))
            .fetch()
            .into(Relation.class);

        return relations.isEmpty() ? Optional.empty() : Optional.of(relations.getFirst());
    }

    /**
     * Method deletes the link entity if no one else is tracking it.
     *
     * @param linkId link ID.
     */
    private void deleteUntraceableLinks(Long linkId) {
        if (isLinkTrack(linkId)) {
            dslContext
                .delete(LINK)
                .where(LINK.ID.eq(linkId))
                .execute();
        }
    }

    /**
     * Method for creating a link entity in the links table.
     * Method gets the content at the start of tracking,
     * which will be stored in json, then saves the link with the content in the database.
     *
     * @return the Link object, which contains the link name and the identifier assigned to it in the database.
     */
    @SneakyThrows
    private Link createLink(URI linkName) {
        var linkContent = objectMapper.writeValueAsString(uriProcessor.processUri(linkName));
        dslContext
            .insertInto(LINK)
            .columns(LINK.LINK_NAME, LINK.CONTENT)
            .values(linkName.toString(), linkContent)
            .execute();
        return getLinkIfExist(linkName);
    }
}