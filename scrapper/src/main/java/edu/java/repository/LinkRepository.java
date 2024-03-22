package edu.java.repository;

import edu.java.models.entities.Link;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import reactor.core.publisher.Mono;

/**
 * contract link repository.
 */
public interface LinkRepository {
    /**
     * Method for searching for all link entities from the links table.
     *
     * @return the list of link entities that are in the table.
     */
    Mono<List<Link>> findAll();

    /**
     * Method of searching for a link from the links table with the specified identifier.
     *
     * @param id link id.
     * @return if there is a link in the table with the specified ID, returns this link otherwise empty Optional.
     */
    Mono<Optional<Link>> findById(Long id);

    /**
     * Method for searching for all link entities from the links table
     * whose last update time is less than specified in the argument
     *
     * @param time time to filter the search.
     * @return the list of link entities that are in the table.
     */
    Mono<List<Link>> findAllByTime(OffsetDateTime time);

    /**
     * Method update method for the link is the value of the last modifying column.
     *
     * @param linkId               link id to update.
     * @param newLastModifyingTime new last modifying value.
     * @return updated link
     */
    Mono<Link> updateLastModifying(Long linkId, OffsetDateTime newLastModifyingTime);

    /**
     * Method update method for the link is the value of the content column.
     *
     * @param linkId     link id to update.
     * @param newContent new content value.
     * @return updated link
     */
    Mono<Link> updateContent(Long linkId, String newContent);

    /**
     * Method of searching for a link by name in the database from the links table
     *
     * @param linkName name link.
     * @return empty Optional if there is no link with the same name in the database, otherwise one Link object.
     */
    Mono<Optional<Link>> findLinkByName(URI linkName);
}
