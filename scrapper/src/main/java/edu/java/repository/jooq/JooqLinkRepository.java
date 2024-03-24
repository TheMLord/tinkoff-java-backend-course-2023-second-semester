package edu.java.repository.jooq;

import edu.java.domain.jooq.pojos.Links;
import edu.java.repository.LinkRepository;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import reactor.core.publisher.Mono;
import static edu.java.domain.jooq.tables.Links.LINKS;

@RequiredArgsConstructor
public class JooqLinkRepository implements LinkRepository {
    private final DSLContext dslContext;

    @Override
    public Mono<List<Links>> findAll() {
        return Mono.just(dslContext
            .select(LINKS.fields())
            .from(LINKS)
            .fetchInto(Links.class));
    }

    @Override
    public Mono<Optional<Links>> findById(Long id) {
        return Mono.defer(() -> {
            var resultLinks = dslContext
                .select(LINKS.fields())
                .from(LINKS)
                .where(LINKS.ID.eq(id))
                .fetchInto(Links.class);
            return resultLinks.isEmpty()
                ? Mono.just(Optional.empty())
                : Mono.just(Optional.of(resultLinks.getFirst()));
        });
    }

    @Override
    public Mono<List<Links>> findAllByTime(OffsetDateTime time) {
        return Mono.just(dslContext
            .select(LINKS.fields())
            .from(LINKS)
            .where(LINKS.LAST_MODIFYING.lt(time))
            .fetchInto(Links.class));
    }

    @Override
    public Mono<Links> updateLastModifying(Long linkId, OffsetDateTime newLastModifyingTime) {
        return Mono.defer(() -> {
            dslContext
                .update(LINKS)
                .set(LINKS.LAST_MODIFYING, newLastModifyingTime)
                .where(LINKS.ID.eq(linkId))
                .execute();
            return findById(linkId).map(Optional::get);
        });
    }

    @Override
    public Mono<Links> updateContent(Long linkId, String newContent) {
        return Mono.defer(() -> {
            dslContext
                .update(LINKS)
                .set(LINKS.CONTENT, newContent)
                .where(LINKS.ID.eq(linkId))
                .execute();
            return findById(linkId).map(Optional::get);
        });
    }

    @Override
    public Mono<Optional<Links>> findLinkByName(URI linkName) {
        return Mono.defer(() -> {
            var resultLinks = dslContext
                .select(LINKS.fields())
                .from(LINKS)
                .where(LINKS.LINK_URI.eq(linkName.toString()))
                .fetchInto(Links.class);
            return resultLinks.isEmpty()
                ? Mono.just(Optional.empty())
                : Mono.just(Optional.of(resultLinks.getFirst()));
        });
    }
}
