package edu.java.repository;

import edu.java.models.entities.Link;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface LinkRepository {
    List<Link> findAll();

    Optional<Link> findById(Long id);

    List<Link> findAllByTime(OffsetDateTime time);

    Link updateLastModifying(Long linkId, OffsetDateTime newLastModifyingTime);

    Link updateContent(Long linkId, String newContent);

    /**
     * Method of searching for a link by name in the database from the links table
     *
     * @param linkName name link.
     * @return empty Optional if there is no link with the same name in the database, otherwise one Link object.
     */
    Optional<Link> findLinkByName(URI linkName);
}
