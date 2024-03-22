package edu.java.controller;

import edu.java.models.dto.api.request.AddLinkRequest;
import edu.java.models.dto.api.request.RemoveLinkRequest;
import edu.java.models.dto.api.response.LinkResponse;
import edu.java.models.dto.api.response.ListLinksResponse;
import edu.java.services.LinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class LinksController implements LinksApi {
    private final LinkService linkService;

    @Override
    public Mono<ResponseEntity<LinkResponse>> linksDelete(
        Long tgChatId,
        RemoveLinkRequest removeLinkRequest
    ) {
        return linkService.removeLink(tgChatId, removeLinkRequest.getLink())
            .flatMap(linkResponse -> Mono.just(ResponseEntity
                .ok()
                .body(linkResponse))
            );

    }

    @Override
    public Mono<ResponseEntity<ListLinksResponse>> linksGet(Long tgChatId) {
        return linkService.getListLinks(tgChatId).flatMap(listLinksResponse -> Mono.just(
            ResponseEntity
                .ok()
                .body(listLinksResponse)
        ));
    }

    @Override
    public Mono<ResponseEntity<LinkResponse>> linksPost(
        Long tgChatId,
        AddLinkRequest addLinkRequest
    ) {
        return linkService.addLink(tgChatId, addLinkRequest.getLink())
            .flatMap(linkResponse -> Mono.just(ResponseEntity.ok().body(linkResponse)));
    }
}
