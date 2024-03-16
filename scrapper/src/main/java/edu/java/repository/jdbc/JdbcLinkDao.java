package edu.java.repository.jdbc;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.exceptions.AlreadyTrackLinkException;
import edu.java.exceptions.NotExistLinkException;
import edu.java.exceptions.NotExistTgChatException;
import edu.java.exceptions.NotTrackLinkException;
import edu.java.models.entities.Link;
import edu.java.models.entities.Relation;
import edu.java.models.entities.TgChat;
import edu.java.processors.UriProcessor;
import edu.java.repository.LinkDao;
import edu.java.repository.LinkRepository;
import edu.java.repository.TgChatRepository;
import edu.java.repository.jdbc.utilities.JdbcRowMapperUtil;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
            jdbcTemplate.update("INSERT INTO relation (chat_id, link_id) VALUES (?, ?)", tgChatId, linkId);
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

        jdbcTemplate.update("DELETE FROM relation WHERE id = (?)", relationId);
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
        return jdbcTemplate.query(
            "SELECT * FROM link WHERE id IN (SELECT relation.link_id FROM relation WHERE chat_id = (?))",
            JdbcRowMapperUtil::mapRowToLink,
            tgChat.getChatId()
        );
    }

    @Override
    public List<Long> findAllIdTgChatWhoTrackLink(Long uriId) {
        return jdbcTemplate.query(
            "SELECT chat_id FROM tgChat WHERE chat_id IN (SELECT chat_id FROM relation WHERE link_id = (?))",
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
        var resultRelations = jdbcTemplate.query(
            "SELECT * FROM relation WHERE chat_id = (?) and link_id = (?)",
            JdbcRowMapperUtil::mapRowToRelation,
            chatId, uriId
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
            jdbcTemplate.update("DELETE FROM link WHERE id = (?)", linkId);
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
        jdbcTemplate.update(
            "INSERT INTO link (link_name, content) VALUES (?, ?)",
            linkName.toString(),
            linkContent
        );
        return getLinkIfExist(linkName);
    }
}