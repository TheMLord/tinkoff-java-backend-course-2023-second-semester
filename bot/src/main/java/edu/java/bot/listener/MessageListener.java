package edu.java.bot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.models.dto.TelegramMessage;
import edu.java.bot.sender.BotMessageSender;
import edu.java.bot.service.MessageService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Class message controller for telegram bot.
 */
@Component
@Slf4j
public class MessageListener implements UpdatesListener {
    private final MessageService messageService;
    private final BotMessageSender botMessageSender;

    public MessageListener(
        TelegramBot telegramBot,
        MessageService messageService,
        BotMessageSender botMessageSender
    ) {
        telegramBot.setUpdatesListener(this);
        this.messageService = messageService;
        this.botMessageSender = botMessageSender;
    }

    /**
     * Method that accepts updates for the bot and responds to these updates
     */
    @Override
    public int process(List<Update> list) {
        Flux<TelegramMessage> messages = Flux.fromIterable(list)
            .flatMap(update -> {
                try {
                    long chatId = update.message().chat().id();
                    var response = messageService.prepareResponseMessage(update);
                    return response.map(r -> new TelegramMessage(r, chatId)).flux();
                } catch (Exception e) {
                    log.error("Error preparing update: {}", e.getMessage());
                    return Flux.empty();
                }
            });

        messages.flatMap(message ->
            botMessageSender.sendMessage(message)
                .doOnSuccess(cancel -> log.info("Message sent successfully to chat {}", message.chatId()))
                .doOnError(error -> log.error(
                    "Error sending message to chat {}: {}",
                    message.chatId(),
                    error.getMessage()
                ))
                .onErrorResume(error -> Mono.empty())
        ).subscribe();

        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
