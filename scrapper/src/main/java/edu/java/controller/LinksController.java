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
        var linkResponse = linkService.removeLink(
            tgChatId,
            removeLinkRequest.getLink()
        );

        return Mono.just(
            ResponseEntity
                .ok()
                .body(linkResponse)
        );
    }

    @Override
    public Mono<ResponseEntity<ListLinksResponse>> linksGet(Long tgChatId) {
        var linksResponse = linkService.getListLinks(tgChatId);

        return Mono.just(
            ResponseEntity
                .ok()
                .body(linksResponse)
        );
    }

    @Override
    public Mono<ResponseEntity<LinkResponse>> linksPost(
        Long tgChatId,
        AddLinkRequest addLinkRequest
    ) {
        var linkResponse = linkService.addLink(
            tgChatId,
            addLinkRequest.getLink()
        );

        return Mono.just(
            ResponseEntity.ok()
                .body(linkResponse)
        );
    }
}
