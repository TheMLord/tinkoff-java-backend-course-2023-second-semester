package edu.java.controller;

import edu.java.models.dto.api.request.AddLinkRequest;
import edu.java.models.dto.api.request.RemoveLinkRequest;
import edu.java.models.dto.api.response.LinkResponse;
import edu.java.models.dto.api.response.ListLinksResponse;
import edu.java.services.LinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LinksController implements LinksApi {
    private final LinkService linkService;

    @Override
    public ResponseEntity<LinkResponse> linksDelete(
        Long tgChatId,
        RemoveLinkRequest removeLinkRequest
    ) {
        var linkResponse = linkService.deleteLink(
            tgChatId,
            removeLinkRequest.getLink()
        );

        return ResponseEntity.ok()
            .body(linkResponse);
    }

    @Override
    public ResponseEntity<ListLinksResponse> linksGet(Long tgChatId) {
        var linksResponse = linkService.prepareUserLinks(tgChatId);

        return ResponseEntity.ok()
            .body(linksResponse);
    }

    @Override
    public ResponseEntity<LinkResponse> linksPost(
        Long tgChatId,
        AddLinkRequest addLinkRequest
    ) {
        var linkResponse = linkService.addLink(
            tgChatId,
            addLinkRequest.getLink()
        );

        return ResponseEntity.ok()
            .body(linkResponse);
    }
}
