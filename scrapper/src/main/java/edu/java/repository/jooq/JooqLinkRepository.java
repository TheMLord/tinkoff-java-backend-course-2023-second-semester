package edu.java.repository.jooq;

import edu.java.domain.jooq.tables.pojos.Link;
import edu.java.repository.LinkRepository;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import static edu.java.domain.jooq.Tables.LINK;

@RequiredArgsConstructor
public class JooqLinkRepository implements LinkRepository {
    private final DSLContext dslContext;

    @Override
    public List<Link> findAll() {
        return dslContext
            .select(LINK.fields())
            .from(LINK)
            .fetch()
            .into(Link.class);
    }

    @Override
    public Optional<Link> findById(Long id) {
        var links = dslContext
            .select(LINK.fields())
            .from(LINK)
            .where(LINK.ID.eq(id))
            .fetch()
            .into(Link.class);
        return links.isEmpty() ? Optional.empty() : Optional.of(links.getFirst());
    }

    @Override
    public List<Link> findAllByTime(OffsetDateTime time) {
        return dslContext
            .select(LINK.fields())
            .from(LINK)
            .where(LINK.LAST_MODIFYING.lt(time))
            .fetch()
            .into(Link.class);
    }

    @Override
    public Link updateLastModifying(Long linkId, OffsetDateTime newLastModifyingTime) {
        dslContext
            .update(LINK)
            .set(LINK.LAST_MODIFYING, newLastModifyingTime)
            .where(LINK.ID.eq(linkId))
            .execute();

        return dslContext
            .select(LINK.fields())
            .from(LINK)
            .where(LINK.ID.eq(linkId))
            .fetch()
            .into(Link.class)
            .getFirst();
    }

    @Override
    public Link updateContent(Long linkId, String newContent) {
        dslContext
            .update(LINK)
            .set(LINK.CONTENT, newContent)
            .where(LINK.ID.eq(linkId))
            .execute();

        return dslContext
            .select(LINK.fields())
            .from(LINK)
            .where(LINK.ID.eq(linkId))
            .fetch()
            .into(Link.class)
            .getFirst();
    }

    @Override
    public Optional<Link> findLinkByName(URI linkName) {
        var links = dslContext
            .select(LINK.fields())
            .from(LINK)
            .where(LINK.LINK_NAME.eq(linkName.toString()))
            .fetch()
            .into(Link.class);
        return links.isEmpty() ? Optional.empty() : Optional.of(links.getFirst());
    }
}
