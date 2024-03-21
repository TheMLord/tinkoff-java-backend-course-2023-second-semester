package edu.java.repository.jdbc;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.exceptions.AlreadyTrackLinkException;
import edu.java.exceptions.NotExistLinkException;
import edu.java.exceptions.NotExistTgChatException;
import edu.java.exceptions.NotTrackLinkException;
import edu.java.models.entities.Link;
import edu.java.models.entities.Subscriptions;
import edu.java.models.entities.TgChat;
import edu.java.processors.UriProcessor;
import edu.java.repository.LinkDao;
import edu.java.repository.LinkRepository;
import edu.java.repository.TgChatRepository;
import edu.java.repository.jdbc.utilities.JdbcRowMapperUtil;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * jdbc implementation link dao.
 */
@Repository
@RequiredArgsConstructor
public class JdbcLinkDao implements LinkDao {
    private final JdbcTemplate jdbcTemplate;
    private final TgChatRepository chatRepository;
    private final LinkRepository linkRepository;
    private final UriProcessor uriProcessor;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public Link add(Long chatId, URI uri) {
        var tgChatId = getChatIfExist(chatId).getId();

        Link link;
        try {
            link = getLinkIfExist(uri);
        } catch (NotExistLinkException e) {
            link = createLink(uri);
        }
        var linkId = link.getId();

        try {
            jdbcTemplate.update(
                "INSERT INTO subscriptions (chat_id, link_id, created_at) VALUES (?, ?, ?)",
                tgChatId,
                linkId,
                OffsetDateTime.now()
            );
            return new Link(
                linkId,
                link.getLinkUri(),
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
        var tgChatId = getChatIfExist(chatId).getId();
        var link = getLinkIfExist(uri);

        var linkId = link.getId();

        jdbcTemplate.update(
            "DELETE FROM subscriptions WHERE link_id = (?)",
            getRelationIfExist(tgChatId, linkId).getLinkId()
        );
        deleteUntraceableLinks(linkId);

        return new Link(
            linkId,
            link.getLinkUri(),
            link.getCreatedAt(),
            link.getCreatedBy(),
            link.getContent(),
            link.getLastModifying()
        );
    }

    @Override
    public List<Link> getAllLinkInRelation(Long chatId) {
        var tgChat = getChatIfExist(chatId);
        return jdbcTemplate.query(
            "SELECT * FROM links WHERE id IN (SELECT link_id FROM subscriptions WHERE chat_id = (?))",
            JdbcRowMapperUtil::mapRowToLink,
            tgChat.getId()
        );
    }

    @Override
    public List<Long> findAllIdTgChatWhoTrackLink(Long uriId) {
        return jdbcTemplate.query(
            "SELECT id FROM tgchats WHERE id IN (SELECT chat_id FROM subscriptions WHERE link_id = (?))",
            JdbcRowMapperUtil::mapRowToChatId,
            uriId
        );
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
    private TgChat getChatIfExist(Long id) {
        return chatRepository.findById(id).orElseThrow(NotExistTgChatException::new);
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
    private Subscriptions getRelationIfExist(Long chatId, Long uriId) {
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
    private Optional<Subscriptions> findRelationBetweenTgChatAndLink(Long chatId, Long uriId) {
        var resultRelations = jdbcTemplate.query(
            "SELECT * FROM subscriptions WHERE chat_id = (?) and link_id = (?)",
            JdbcRowMapperUtil::mapRowToRelation,
            chatId,
            uriId
        );
        return resultRelations.isEmpty() ? Optional.empty() : Optional.of(resultRelations.getFirst());
    }

    /**
     * Method deletes the link entity if no one else is tracking it.
     *
     * @param linkId link ID.
     */
    private void deleteUntraceableLinks(Long linkId) {
        if (isLinkTrack(linkId)) {
            jdbcTemplate.update("DELETE FROM links WHERE id = (?)", linkId);
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
    private Link createLink(URI linkUri) {
        var linkContent = objectMapper.writeValueAsString(uriProcessor.processUri(linkUri));
        jdbcTemplate.update(
            "INSERT INTO links (link_uri, created_at, content) VALUES (?, ?, ?)",
            linkUri.toString(),
            OffsetDateTime.now(),
            linkContent
        );
        return getLinkIfExist(linkUri);
    }
}
