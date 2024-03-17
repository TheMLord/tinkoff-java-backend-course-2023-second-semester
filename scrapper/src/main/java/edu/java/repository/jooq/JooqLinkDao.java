package edu.java.repository.jooq;

import edu.java.domain.jooq.tables.pojos.Link;
import edu.java.exceptions.AlreadyTrackLinkException;
import edu.java.exceptions.NotExistLinkException;
import edu.java.exceptions.NotExistTgChatException;
import edu.java.exceptions.NotTrackLinkException;
import edu.java.repository.LinkDao;
import edu.java.repository.LinkRepository;
import edu.java.repository.TgChatRepository;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
public class JooqLinkDao implements LinkDao {
    private final LinkRepository jooqLinkRepository;
    private final TgChatRepository jooqThChatRepository;
    private final DSLContext dslContext;

    @Override
    public Link add(Long chatId, URI uri) throws NotExistTgChatException, AlreadyTrackLinkException {
        return null;
    }

    @Override
    public Link remove(Long chatId, URI uri)
        throws NotExistTgChatException, NotExistLinkException, NotTrackLinkException {
        return null;
    }

    @Override
    public List<Link> getAllLinkInRelation(Long chatId) throws NotExistTgChatException {
        return null;
    }

    @Override
    public List<Long> findAllIdTgChatWhoTrackLink(Long uriId) {
        return null;
    }
}
