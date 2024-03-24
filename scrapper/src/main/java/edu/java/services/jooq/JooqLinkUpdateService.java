package edu.java.services.jooq;

import edu.java.models.dto.api.LinkUpdate;
import edu.java.processors.UriProcessor;
import edu.java.repository.LinkDao;
import edu.java.repository.LinkRepository;
import edu.java.services.LinkUpdateService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class JooqLinkUpdateService implements LinkUpdateService {
    private final UriProcessor uriProcessor;
    private final LinkDao linkDao;
    private final LinkRepository linkRepository;

    @Override
    @Transactional
    public Flux<Optional<LinkUpdate>> prepareLinkUpdate() {
        return linkRepository.findAllByTime(OffsetDateTime.now().minusHours(1))
            .flatMapMany(Flux::fromIterable)
            .flatMap(link -> {
                var linkId = link.getId();
                var updates =
                    uriProcessor.compareContent(URI.create(link.getLinkUri()), link.getContent());
                return updates.map(linkChanges -> linkRepository.updateLastModifying(linkId, OffsetDateTime.now())
                    .flatMapMany(updatedLink -> linkRepository.updateContent(linkId, linkChanges.newContent())
                        .thenMany(linkDao.findAllIdTgChatWhoTrackLink(linkId)
                            .map(listSubscribers -> Optional.of(new LinkUpdate(
                                linkId,
                                linkChanges.linkName(),
                                linkChanges.descriptionChanges(),
                                listSubscribers
                            )))
                        )
                    )).orElseGet(() -> linkRepository.updateLastModifying(linkId, OffsetDateTime.now())
                    .flatMapMany(updatedLink -> Mono.just(Optional.empty())));
            });
    }
}
