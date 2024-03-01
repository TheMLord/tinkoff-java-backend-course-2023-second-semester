package edu.java.bot.controller;

import edu.java.bot.controller.dto.LinkUpdate;
import edu.java.bot.model.TelegramMessage;
import edu.java.bot.sender.BotMessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class BotController implements UpdatesApi {
    private final BotMessageSender botMessageSender;

    @Override
    public Mono<ResponseEntity<Void>> updatesPost(Mono<LinkUpdate> linkUpdate, ServerWebExchange exchange) {
        botMessageSender.sendMessage(
            linkUpdate.flatMapMany(update -> {
                var messageDescription = new TelegramMessage(update.getDescription(), update.getId());
                var messageWithUrl = new TelegramMessage(update.getUrl().toString(), update.getId());
                return Flux.just(messageDescription, messageWithUrl);
            })
        );
        return Mono.just(
            ResponseEntity
                .ok()
                .build()
        );
    }
}
