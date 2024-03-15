package edu.java.repository.jdbc;

import edu.java.models.entities.Link;
import edu.java.repository.LinkRepository;
import java.net.URI;
import java.sql.ResultSet;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class jdbcLinkRepository implements LinkRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Link> findAll() {
        return jdbcTemplate.query(
            "SELECT * FROM link",
            this::mapRowToLink
        );
    }

    @Override
    public Optional<Link> findById(Long id) {
        var resultLinks = jdbcTemplate.query(
            "SELECT * FROM link WHERE id = (?)",
            this::mapRowToLink,
            id
        );
        return resultLinks.isEmpty() ? Optional.empty() : Optional.of(resultLinks.getFirst());
    }

    @Override
    public List<Link> findAllByTime(OffsetDateTime time) {
        return jdbcTemplate.query(
            "SELECT * FROM link WHERE last_modifying < (?)",
            this::mapRowToLink,
            time
        );
    }

    @Override
    public Link updateLastModifying(Long linkId, OffsetDateTime newLastModifyingTime) {
        jdbcTemplate.update(
            "UPDATE link SET last_modifying = (?) WHERE id = (?)",
            newLastModifyingTime,
            linkId
        );
        return findById(linkId).get();
    }

    @Override
    public Link updateContent(Long linkId, String newContent) {
        jdbcTemplate.update(
            "UPDATE link SET content = (?) WHERE id = (?)",
            newContent,
            linkId
        );
        return findById(linkId).get();
    }

    @Override
    public Optional<Link> findLinkByName(URI linkName) {
        var resultLinks = jdbcTemplate.query(
            "SELECT * FROM link WHERE link_name = (?)",
            this::mapRowToLink,
            linkName.toString()
        );
        return resultLinks.isEmpty() ? Optional.empty() : Optional.of(resultLinks.getFirst());
    }

    /**
     * Method of mapping the string received from the query to the Link entity.
     *
     * @param row    row set from the table.
     * @param rowNum number row from the received data from the query.
     * @return Link entity.
     */
    @SneakyThrows
    private Link mapRowToLink(ResultSet row, int rowNum) {
        return new Link(
            row.getLong("id"),
            URI.create(row.getString("link_name")),
            row.getObject("created_at", OffsetDateTime.class),
            row.getString("created_by"),
            row.getString("content"),
            row.getObject("last_modifying", OffsetDateTime.class)
        );
    }

}
