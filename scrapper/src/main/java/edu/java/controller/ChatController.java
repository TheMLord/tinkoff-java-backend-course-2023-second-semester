package edu.java.controller;

import api.TgChatApi;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Controller
public class ChatController implements TgChatApi {
    @Override
    public Mono<ResponseEntity<Void>> tgChatIdDelete(Long id, ServerWebExchange exchange) {
        return TgChatApi.super.tgChatIdDelete(id, exchange);
    }

    @Override
    public Mono<ResponseEntity<Void>> tgChatIdPost(Long id, ServerWebExchange exchange) {
        return TgChatApi.super.tgChatIdPost(id, exchange);
    }
}
