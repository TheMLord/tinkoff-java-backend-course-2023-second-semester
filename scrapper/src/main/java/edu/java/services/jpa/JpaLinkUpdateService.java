package edu.java.services.jpa;

import edu.java.domain.jpa.Subscriptions;
import edu.java.domain.jpa.TgChats;
import edu.java.models.dto.api.LinkUpdate;
import edu.java.processors.UriProcessor;
import edu.java.repository.jpa.JpaLinkRepository;
import edu.java.repository.jpa.JpaSubscriptionRepository;
import edu.java.services.LinkUpdateService;
import java.net.URI;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class JpaLinkUpdateService implements LinkUpdateService {
    private final JpaLinkRepository jpaLinkRepository;
    private final UriProcessor uriProcessor;
    private final JpaSubscriptionRepository jpaSubscriptionRepository;

    @Override
    public Flux<LinkUpdate> prepareLinkUpdate() {
        return Mono.just(jpaLinkRepository.findAllByLastModifyingBefore(OffsetDateTime.now().minusHours(1)))
            .flatMapMany(Flux::fromIterable)
            .flatMap(link -> {
                    link.setLastModifying(OffsetDateTime.now());
                    jpaLinkRepository.saveAndFlush(link);

                    return uriProcessor.compareContent(URI.create(link.getLinkUri()), link.getContent())
                        .flatMapMany(linkChanges -> {
                            link.setContent(linkChanges.newContent());
                            jpaLinkRepository.saveAndFlush(link);

                            return Flux.just(new LinkUpdate(
                                    link.getId(),
                                    linkChanges.linkName(),
                                    linkChanges.descriptionChanges(),
                                    jpaSubscriptionRepository.findAllByLink(link).stream()
                                        .map(Subscriptions::getChat)
                                        .map(TgChats::getId)
                                        .toList()
                                )
                            );
                        });
                }
            );
    }
}
