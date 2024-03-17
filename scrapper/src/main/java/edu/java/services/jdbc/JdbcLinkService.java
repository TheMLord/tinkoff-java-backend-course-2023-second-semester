package edu.java.services.jdbc;

import edu.java.models.dto.api.response.LinkResponse;
import edu.java.models.dto.api.response.ListLinksResponse;
import edu.java.repository.LinkDao;
import edu.java.services.LinkService;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the jdbc link service.
 */
@Service
@RequiredArgsConstructor
public class JdbcLinkService implements LinkService {
    private final LinkDao linkDao;

    @Override
    @Transactional
    public LinkResponse addLink(long chatId, URI linkUri) {
        var link = linkDao.add(chatId, linkUri);
        return new LinkResponse(link.getId(), URI.create(link.getLinkName()));
    }

    @Override
    @Transactional
    public LinkResponse removeLink(long chatId, URI linkUri) {
        var link = linkDao.remove(chatId, linkUri);
        return new LinkResponse(link.getId(), URI.create(link.getLinkName()));
    }

    @Override
    @Transactional
    public ListLinksResponse getListLinks(long chatId) {
        var links = linkDao.getAllLinkInRelation(chatId);

        return new ListLinksResponse(
            links.stream().map(link -> new LinkResponse(link.getId(), URI.create(link.getLinkName()))).toList(),
            links.size()
        );
    }
}
