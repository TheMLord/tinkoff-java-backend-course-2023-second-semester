package edu.java.bot.controller;

import edu.java.bot.models.dto.TelegramMessage;
import edu.java.bot.models.dto.api.LinkUpdate;
import edu.java.bot.sender.BotMessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class UpdateController implements UpdatesApi {
    private final BotMessageSender botMessageSender;

    @Override
    public ResponseEntity<Void> updatesPost(Mono<LinkUpdate> linkUpdate) {
        botMessageSender.sendMessage(
            linkUpdate.flatMapMany(update -> {
                var listIds = update.getTgChatIds();
                var description = update.getDescription();
                var uri = update.getUrl().toString();

                return Flux.fromIterable(listIds).map(id ->
                    new TelegramMessage("%s. Ссылка %s".formatted(description, uri), id));
            })
        );
        return ResponseEntity
                .ok()
                .build();
    }
}
