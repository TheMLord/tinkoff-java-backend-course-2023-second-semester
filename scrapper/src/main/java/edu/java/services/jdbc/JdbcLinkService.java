package edu.java.services.jdbc;

import edu.java.models.dto.api.response.LinkResponse;
import edu.java.models.dto.api.response.ListLinksResponse;
import edu.java.repository.LinkDao;
import edu.java.services.LinkService;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/**
 * Implementation of the jdbc link service.
 */
@Service
@RequiredArgsConstructor
public class JdbcLinkService implements LinkService {
    private final LinkDao linkDao;

    @Override
    @Transactional
    public Mono<LinkResponse> addLink(long chatId, URI linkUri) {
        return linkDao.add(chatId, linkUri)
            .flatMap(link -> Mono.just(
                    new LinkResponse(
                        link.getId(),
                        link.getLinkUri()
                    )
                )
            );
    }

    @Override
    @Transactional
    public Mono<LinkResponse> removeLink(long chatId, URI linkUri) {
        return linkDao.remove(chatId, linkUri)
            .flatMap(link -> Mono.just(new LinkResponse(link.getId(), link.getLinkUri())));

    }

    @Override
    @Transactional
    public Mono<ListLinksResponse> getListLinks(long chatId) {
        return linkDao.getAllLinksInRelation(chatId).collectList()
            .map(links -> links.stream().map(link -> new LinkResponse(link.getId(), link.getLinkUri())).toList())
            .flatMap(linkResponses -> Mono.just(new ListLinksResponse(linkResponses, linkResponses.size())));
    }
}
