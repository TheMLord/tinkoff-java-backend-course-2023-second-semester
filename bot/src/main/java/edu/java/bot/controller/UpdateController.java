package edu.java.bot.controller;

import edu.java.bot.models.dto.TelegramMessage;
import edu.java.bot.models.dto.api.LinkUpdate;
import edu.java.bot.sender.BotMessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controller that receives information about content updates in links.
 */
@RestController
@RequiredArgsConstructor
public class UpdateController implements UpdatesApi {
    private final BotMessageSender botMessageSender;

    @Override
    public Mono<ResponseEntity<Void>> updatesPost(Flux<LinkUpdate> linkUpdate) {
        return linkUpdate.flatMap(update -> {
                var listIds = update.getTgChatIds();
                var description = update.getDescription();
                var uri = update.getUrl().toString();

                return Flux.fromIterable(listIds)
                    .map(id -> new TelegramMessage("%s. Ссылка %s".formatted(description, uri), id));
            }).map(botMessageSender::sendMessage)
            .then(Mono.just(ResponseEntity.ok().build()));
    }
}
