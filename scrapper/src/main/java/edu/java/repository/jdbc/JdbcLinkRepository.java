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
import reactor.core.publisher.Mono;

/**
 * jdbc implementation link repository
 */
@Repository
@RequiredArgsConstructor
public class JdbcLinkRepository implements LinkRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Mono<List<Link>> findAll() {
        return Mono.just(jdbcTemplate.query(
            "SELECT * FROM links",
            JdbcRowMapperUtil::mapRowToLink
        ));
    }

    @Override
    public Mono<Optional<Link>> findById(Long id) {
        return Mono.defer(() -> {
            var resultLinks = jdbcTemplate.query(
                "SELECT * FROM links WHERE id = (?)",
                JdbcRowMapperUtil::mapRowToLink,
                id
            );
            return resultLinks.isEmpty()
                ? Mono.just(Optional.empty())
                : Mono.just(Optional.of(resultLinks.getFirst()));
        });
    }

    @Override
    public Mono<List<Link>> findAllByTime(OffsetDateTime time) {
        return Mono.just(jdbcTemplate.query(
            "SELECT * FROM links WHERE last_modifying < (?)",
            JdbcRowMapperUtil::mapRowToLink,
            time
        ));
    }

    @Override
    public Mono<Link> updateLastModifying(Long linkId, OffsetDateTime newLastModifyingTime) {
        return Mono.defer(() -> {
            jdbcTemplate.update(
                "UPDATE links SET last_modifying = (?) WHERE id = (?)",
                newLastModifyingTime,
                linkId
            );
            return findById(linkId).map(Optional::get);
        });
    }

    @Override
    public Mono<Link> updateContent(Long linkId, String newContent) {
        return Mono.defer(() -> {
            jdbcTemplate.update(
                "UPDATE links SET content = (?) WHERE id = (?)",
                newContent,
                linkId
            );
            return findById(linkId).map(Optional::get);
        });
    }

    @Override
    public Mono<Optional<Link>> findLinkByName(URI linkName) {
        return Mono.defer(() -> {
            var resultLinks = jdbcTemplate.query(
                "SELECT * FROM links WHERE link_uri = (?)",
                JdbcRowMapperUtil::mapRowToLink,
                linkName.toString()
            );
            return resultLinks.isEmpty()
                ? Mono.just(Optional.empty())
                : Mono.just(Optional.of(resultLinks.getFirst()));
        });
    }
}
