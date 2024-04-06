package edu.java.services.jpa;

import edu.java.domain.jpa.Subscriptions;
import edu.java.domain.jpa.TgChats;
import edu.java.models.dto.api.LinkUpdate;
import edu.java.processors.UriProcessor;
import edu.java.repository.jpa.JpaLinkRepository;
import edu.java.repository.jpa.JpaSubscriptionRepository;
import edu.java.services.LinkUpdateCheckService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class JpaLinkUpdateService implements LinkUpdateCheckService {
    private final JpaLinkRepository jpaLinkRepository;
    private final UriProcessor uriProcessor;
    private final JpaSubscriptionRepository jpaSubscriptionRepository;

    @Override
    public Flux<Optional<LinkUpdate>> prepareLinkUpdate() {
        return Mono.just(jpaLinkRepository.findAllByLastModifyingBefore(OffsetDateTime.now().minusHours(1)))
            .flatMapMany(Flux::fromIterable)
            .flatMap(link -> {
                    var linkId = link.getId();
                    var updates = uriProcessor.compareContent(URI.create(link.getLinkUri()), link.getContent());
                    return updates.map(linkChanges -> {
                        link.setLastModifying(OffsetDateTime.now());
                        return Mono.just(jpaLinkRepository.saveAndFlush(link))
                            .flatMapMany(updatedLink -> {
                                link.setContent(linkChanges.newContent());
                                return Mono.just(jpaLinkRepository.saveAndFlush(link))
                                    .thenMany(Mono.just(jpaSubscriptionRepository.findAllByLink(link).stream()
                                        .map(Subscriptions::getChat).map(TgChats::getId)
                                        .toList()).map(subscribers -> Optional.of(new LinkUpdate(
                                        linkId,
                                        linkChanges.linkName(),
                                        linkChanges.descriptionChanges(),
                                        subscribers
                                    ))));

                            });
                    }).orElseGet(() -> {
                            link.setLastModifying(OffsetDateTime.now());
                            return Mono.just(jpaLinkRepository.saveAndFlush(link))
                                .flatMapMany(updatedLink -> Mono.just(Optional.empty()));
                        }
                    );
                }
            );
    }
}
