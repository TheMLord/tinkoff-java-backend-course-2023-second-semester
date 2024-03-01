package edu.java.bot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.exceptions.InvalidUpdateException;
import edu.java.bot.model.TelegramMessage;
import edu.java.bot.sender.BotMessageSender;
import edu.java.bot.service.MessagePrepareService;
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
    private final MessagePrepareService messageService;
    private final BotMessageSender botMessageSender;

    public MessageListener(
        TelegramBot telegramBot,
        MessagePrepareService messageService,
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
        var messages = Flux.fromIterable(list)
            .flatMap(update -> {
                try {
                    var chatId = Mono.just(update.message().chat().id());
                    var response = messageService.prepareResponseMessage(update);
                    return response.flatMapMany(r -> chatId.map(ch -> new TelegramMessage(r, ch)));
                } catch (Exception e) {
                    log.error("Error preparing update: {}", e.getMessage());
                    return Flux.empty();
                }
            });

        try {
            botMessageSender.sendMessage(messages);
        } catch (InvalidUpdateException e) {
            log.error("Error sending update: {}", e.getMessage());
        }

        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
