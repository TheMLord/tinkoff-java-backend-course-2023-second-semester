package edu.java.services.jooq;

import edu.java.models.dto.api.LinkUpdate;
import edu.java.processors.UriProcessor;
import edu.java.repository.LinkDao;
import edu.java.repository.LinkRepository;
import edu.java.services.LinkUpdateService;
import jakarta.transaction.Transactional;
import java.net.URI;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class JooqLinkUpdateService implements LinkUpdateService {
    private final UriProcessor uriProcessor;
    private final LinkDao linkDao;
    private final LinkRepository linkRepository;

    @Override
    @Transactional
    public Flux<LinkUpdate> prepareLinkUpdate() {
        return linkRepository.findAllByTime(OffsetDateTime.now().minusHours(1))
            .flatMap(link -> {
                    var linkId = link.getId();
                    linkRepository.updateLastModifying(linkId, OffsetDateTime.now());
                    return uriProcessor.compareContent(URI.create(link.getLinkUri()), link.getContent())
                        .flatMapMany(linkChanges -> linkRepository.updateContent(linkId, linkChanges.newContent())
                            .thenMany(linkDao.findAllIdTgChatWhoTrackLink(linkId)
                                .collectList()
                                .flatMapMany(subscribers -> Flux.just(new LinkUpdate(
                                    linkId,
                                    linkChanges.linkName(),
                                    linkChanges.descriptionChanges(),
                                    subscribers
                                )))));

                }
            );
    }
}
