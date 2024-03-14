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
        return ResponseEntity.ok()
            .body(linkService.removeLink(tgChatId, removeLinkRequest.getLink()));
    }

    @Override
    public ResponseEntity<ListLinksResponse> linksGet(Long tgChatId) {
        return ResponseEntity.ok()
            .body(linkService.getListLinks(tgChatId));
    }

    @Override
    public ResponseEntity<LinkResponse> linksPost(
        Long tgChatId,
        AddLinkRequest addLinkRequest
    ) {
        return ResponseEntity.ok()
            .body(linkService.addLink(tgChatId, addLinkRequest.getLink()));
    }
}
