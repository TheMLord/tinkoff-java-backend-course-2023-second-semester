package edu.java.repository.jdbc;

import edu.java.models.entities.Link;
import edu.java.repository.LinkRepository;
import edu.java.repository.jdbc.utilities.JdbcRowMapperUtil;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * jdbc implementation link repository
 */
@Repository
@RequiredArgsConstructor
public class JdbcLinkRepository implements LinkRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Link> findAll() {
        return jdbcTemplate.query(
            "SELECT * FROM links",
            JdbcRowMapperUtil::mapRowToLink
        );
    }

    @Override
    public Optional<Link> findById(Long id) {
        var resultLinks = jdbcTemplate.query(
            "SELECT * FROM links WHERE id = (?)",
            JdbcRowMapperUtil::mapRowToLink,
            id
        );
        return resultLinks.isEmpty() ? Optional.empty() : Optional.of(resultLinks.getFirst());
    }

    @Override
    public List<Link> findAllByTime(OffsetDateTime time) {
        return jdbcTemplate.query(
            "SELECT * FROM links WHERE last_modifying < (?)",
            JdbcRowMapperUtil::mapRowToLink,
            time
        );
    }

    @Override
    public Link updateLastModifying(Long linkId, OffsetDateTime newLastModifyingTime) {
        jdbcTemplate.update(
            "UPDATE links SET last_modifying = (?) WHERE id = (?)",
            newLastModifyingTime,
            linkId
        );
        return findById(linkId).get();
    }

    @Override
    public Link updateContent(Long linkId, String newContent) {
        jdbcTemplate.update(
            "UPDATE links SET content = (?) WHERE id = (?)",
            newContent,
            linkId
        );
        return findById(linkId).get();
    }

    @Override
    public Optional<Link> findLinkByName(URI linkName) {
        var resultLinks = jdbcTemplate.query(
            "SELECT * FROM links WHERE link_uri = (?)",
            JdbcRowMapperUtil::mapRowToLink,
            linkName.toString()
        );
        return resultLinks.isEmpty() ? Optional.empty() : Optional.of(resultLinks.getFirst());
    }
}
