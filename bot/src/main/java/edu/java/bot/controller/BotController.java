package edu.java.bot.controller;

import api.UpdatesApi;
import model.LinkUpdate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
public class BotController implements UpdatesApi {
    @Override
    public Mono<ResponseEntity<Void>> updatesPost(Mono<LinkUpdate> linkUpdate, ServerWebExchange exchange) {
        return UpdatesApi.super.updatesPost(linkUpdate, exchange);
    }
}
