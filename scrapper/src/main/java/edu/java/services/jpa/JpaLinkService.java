package edu.java.services.jpa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.domain.jpa.Links;
import edu.java.domain.jpa.Subscriptions;
import edu.java.exceptions.AlreadyTrackLinkException;
import edu.java.exceptions.NotExistLinkException;
import edu.java.exceptions.NotExistTgChatException;
import edu.java.exceptions.NotTrackLinkException;
import edu.java.models.dto.api.response.LinkResponse;
import edu.java.models.dto.api.response.ListLinksResponse;
import edu.java.processors.UriProcessor;
import edu.java.repository.jpa.JpaLinkRepository;
import edu.java.repository.jpa.JpaSubscriptionRepository;
import edu.java.repository.jpa.JpaTgChatRepository;
import edu.java.services.LinkService;
import java.net.URI;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class JpaLinkService implements LinkService {
    private final JpaLinkRepository jpaLinkRepository;
    private final JpaTgChatRepository jpaTgChatRepository;
    private final JpaSubscriptionRepository jpaSubscriptionRepository;
    private final UriProcessor uriProcessor;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public Mono<LinkResponse> addLink(long chatId, URI linkUri) {
        var tgChatsOptional = jpaTgChatRepository.findById(chatId);

        if (tgChatsOptional.isEmpty()) {
            return Mono.error(new NotExistTgChatException());
        }

        var tgChat = tgChatsOptional.get();
        var link = jpaLinkRepository.findByLinkUri(linkUri.toString())
            .orElseGet(() -> {
                Links newLink = new Links();
                newLink.setLinkUri(linkUri.toString());
                newLink.setLastModifying(OffsetDateTime.now());
                newLink.setCreatedAt(OffsetDateTime.now());
                try {
                    newLink.setContent(objectMapper.writeValueAsString(uriProcessor.processUri(linkUri)));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                newLink.setCreatedAt(OffsetDateTime.now());
                return jpaLinkRepository.saveAndFlush(newLink);
            });

        var subscriptionOptional = jpaSubscriptionRepository.findByChatAndLink(tgChat, link);

        if (subscriptionOptional.isPresent()) {
            return Mono.error(new AlreadyTrackLinkException());
        }

        var subscription = new Subscriptions();
        subscription.setLink(link);
        subscription.setChat(tgChat);
        subscription.setCreatedAt(OffsetDateTime.now());
        jpaSubscriptionRepository.saveAndFlush(subscription);

        return Mono.just(new LinkResponse(link.getId(), linkUri));
    }

    @Override
    @Transactional
    public Mono<LinkResponse> removeLink(long chatId, URI linkUri) {
        var tgChatsOptional = jpaTgChatRepository.findById(chatId);

        if (tgChatsOptional.isEmpty()) {
            return Mono.error(new NotExistTgChatException());
        }
        var linkOptional = jpaLinkRepository.findByLinkUri(linkUri.toString());

        if (linkOptional.isEmpty()) {
            return Mono.error(new NotExistLinkException());
        }

        var link = linkOptional.get();
        var tgChat = tgChatsOptional.get();

        var subscriptionOption = jpaSubscriptionRepository.findByChatAndLink(tgChat, link);

        if (subscriptionOption.isEmpty()) {
            return Mono.error(new NotTrackLinkException());
        }

        jpaSubscriptionRepository.deleteByChatAndLink(tgChat, link);

        if (jpaSubscriptionRepository.findAllByLink(link).isEmpty()) {
            jpaLinkRepository.deleteById(link.getId());
        }
        return Mono.just(new LinkResponse(link.getId(), URI.create(link.getLinkUri())));
    }

    @Override
    @Transactional
    public Mono<ListLinksResponse> getListLinks(long chatId) {
        var tgChatsOptional = jpaTgChatRepository.findById(chatId);

        if (tgChatsOptional.isEmpty()) {
            return Mono.error(new NotExistTgChatException());
        }

        var links = jpaSubscriptionRepository.findAllByChat(tgChatsOptional.get()).stream()
            .map(Subscriptions::getLink)
            .map(link -> new LinkResponse(link.getId(), URI.create(link.getLinkUri())))
            .toList();

        return Mono.just(new ListLinksResponse(links, links.size()));
    }
}
