package edu.java.repository.jdbc;

import edu.java.domain.jooq.pojos.Links;
import edu.java.exceptions.NotExistLinkException;
import edu.java.repository.LinkRepository;
import edu.java.repository.jdbc.utilities.JdbcRowMapperUtil;
import java.net.URI;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * jdbc implementation link repository
 */
@RequiredArgsConstructor
public class JdbcLinkRepository implements LinkRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Flux<Links> findAll() {
        return Flux.fromIterable(jdbcTemplate.query(
            "SELECT * FROM links",
            JdbcRowMapperUtil::mapRowToLink
        ));
    }

    @Override
    public Mono<Links> findById(Long id) {
        return Mono.defer(() -> {
            var resultLinks = jdbcTemplate.query(
                "SELECT * FROM links WHERE id = (?)",
                JdbcRowMapperUtil::mapRowToLink,
                id
            );
            return resultLinks.isEmpty()
                ? Mono.error(new NotExistLinkException())
                : Mono.just(resultLinks.getFirst());
        });
    }

    @Override
    public Flux<Links> findAllByTime(OffsetDateTime time) {
        return Flux.fromIterable(jdbcTemplate.query(
            "SELECT * FROM links WHERE last_modifying < (?)",
            JdbcRowMapperUtil::mapRowToLink,
            time
        ));
    }

    @Override
    public Mono<Links> updateLastModifying(Long linkId, OffsetDateTime newLastModifyingTime) {
        return Mono.defer(() -> {
            jdbcTemplate.update(
                "UPDATE links SET last_modifying = (?) WHERE id = (?)",
                newLastModifyingTime,
                linkId
            );
            return findById(linkId);
        });
    }

    @Override
    public Mono<Links> updateContent(Long linkId, String newContent) {
        return Mono.defer(() -> {
            jdbcTemplate.update(
                "UPDATE links SET content = (?) WHERE id = (?)",
                newContent,
                linkId
            );
            return findById(linkId);
        });
    }

    @Override
    public Mono<Links> findLinkByName(URI linkName) {
        return Mono.defer(() -> {
            var resultLinks = jdbcTemplate.query(
                "SELECT * FROM links WHERE link_uri = (?)",
                JdbcRowMapperUtil::mapRowToLink,
                linkName.toString()
            );
            return resultLinks.isEmpty()
                ? Mono.error(new NotExistLinkException())
                : Mono.just(resultLinks.getFirst());
        });
    }
}
