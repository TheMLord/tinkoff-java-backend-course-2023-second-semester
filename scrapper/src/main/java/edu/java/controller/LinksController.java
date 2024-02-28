package edu.java.controller;

import api.LinksApi;
import model.AddLinkRequest;
import model.LinkResponse;
import model.ListLinksResponse;
import model.RemoveLinkRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Controller
public class LinksController implements LinksApi {
    @Override
    public Mono<ResponseEntity<LinkResponse>> linksDelete(
        Long tgChatId,
        Mono<RemoveLinkRequest> removeLinkRequest,
        ServerWebExchange exchange
    ) {
        return LinksApi.super.linksDelete(tgChatId, removeLinkRequest, exchange);
    }

    @Override
    public Mono<ResponseEntity<ListLinksResponse>> linksGet(Long tgChatId, ServerWebExchange exchange) {
        return LinksApi.super.linksGet(tgChatId, exchange);
    }

    @Override
    public Mono<ResponseEntity<LinkResponse>> linksPost(
        Long tgChatId,
        Mono<AddLinkRequest> addLinkRequest,
        ServerWebExchange exchange
    ) {
        return LinksApi.super.linksPost(tgChatId, addLinkRequest, exchange);
    }
}
