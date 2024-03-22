package edu.java.services.jooq;

import edu.java.domain.pojos.Links;
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
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class JooqLinkUpdateService implements LinkUpdateService {
    private final UriProcessor uriProcessor;
    private final LinkDao linkDao;
    private final LinkRepository linkRepository;

    @Override
    @Transactional
    public Mono<Optional<LinkUpdate>> prepareLinkUpdate(Links link) {
        var linkId = link.getId();

        return Mono.defer(() -> {
            var updates = uriProcessor.compareContent(URI.create(link.getLinkUri()), link.getContent());
            return updates.map(linkChanges -> linkRepository.updateLastModifying(linkId, OffsetDateTime.now())
                .flatMap(linkWithUpdateModifying -> linkRepository.updateContent(linkId, linkChanges.newContent()))
                .flatMap(linkWithUpdateContent ->
                    linkDao.findAllIdTgChatWhoTrackLink(linkWithUpdateContent.getId()))
                .flatMap(listSubscribers -> Mono.just(Optional.of(new LinkUpdate(
                    linkId,
                    updates.get().linkName(),
                    updates.get().descriptionChanges(),
                    listSubscribers
                ))))).orElseGet(() -> linkRepository.updateLastModifying(linkId, OffsetDateTime.now())
                .flatMap(linkWithUpdateModifying -> Mono.just(Optional.empty())));
        });
    }
}
