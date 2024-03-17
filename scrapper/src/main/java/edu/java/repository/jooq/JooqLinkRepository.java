package edu.java.repository.jooq;

import edu.java.domain.jooq.tables.pojos.Link;
import edu.java.repository.LinkRepository;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

@RequiredArgsConstructor
public class JooqLinkRepository implements LinkRepository {
    private final DSLContext dslContext;

    @Override
    public List<Link> findAll() {
        return null;
    }

    @Override
    public Optional<Link> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Link> findAllByTime(OffsetDateTime time) {
        return null;
    }

    @Override
    public Link updateLastModifying(Long linkId, OffsetDateTime newLastModifyingTime) {
        return null;
    }

    @Override
    public Link updateContent(Long linkId, String newContent) {
        return null;
    }

    @Override
    public Optional<Link> findLinkByName(URI linkName) {
        return Optional.empty();
    }
}
