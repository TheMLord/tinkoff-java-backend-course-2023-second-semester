package edu.java.services.jdbc;

import edu.java.models.dto.api.LinkUpdate;
import edu.java.models.entities.Link;
import edu.java.processors.UriProcessor;
import edu.java.repository.LinkDao;
import edu.java.repository.LinkRepository;
import edu.java.services.LinkUpdateService;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class JdbcLinkUpdateService implements LinkUpdateService {
    private final UriProcessor uriProcessor;
    private final LinkDao linkDao;
    private final LinkRepository linkRepository;

    @Override
    @Transactional
    public Mono<LinkUpdate> prepareLinkUpdate(Link link) {
        var linkId = link.getId();

        return linkRepository.updateLastModifying(linkId, OffsetDateTime.now()).flatMap(updateTimeLink ->
            uriProcessor.compareContent(link.getLinkUri(), link.getContent())
                .flatMap(linkChanges -> linkDao.findAllIdTgChatWhoTrackLink(linkId)
                    .collectList()
                    .map(listSubscribers -> new LinkUpdate(
                        linkId,
                        linkChanges.linkName(),
                        linkChanges.descriptionChanges(),
                        listSubscribers
                    ))
                    .flatMap(linkUpdate ->
                        linkRepository.updateContent(linkId, linkChanges.newContent())
                            .map(link2 -> linkUpdate)
                    )
                )
        );
    }
}
