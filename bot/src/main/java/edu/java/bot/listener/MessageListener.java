package edu.java.bot.listener;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import edu.java.bot.service.MessageService;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Class message controller for telegram bot.
 */
@Component
@Slf4j
public class MessageListener implements UpdatesListener {
    private final TelegramBot telegramBot;
    private final MessageService messageService;

    public MessageListener(TelegramBot telegramBot, MessageService messageService) {
        telegramBot.setUpdatesListener(this);
        this.telegramBot = telegramBot;
        this.messageService = messageService;
    }

    /**
     * Method that accepts updates for the bot and responds to these updates
     */
    @Override
    public int process(List<Update> list) {
        list.forEach(update -> {
                try {
                    var message = new SendMessage(
                        update.message().chat().id(),
                        messageService.prepareResponseMessage(update)
                    );

                    telegramBot.execute(
                        message,
                        new Callback<SendMessage, SendResponse>() {
                            @Override
                            public void onResponse(SendMessage request, SendResponse response) {
                                log.info("Отправка ответа %s на запрос  %s".formatted(
                                    request.toString(),
                                    response.message().text()
                                ));
                            }

                            @Override
                            public void onFailure(SendMessage request, IOException e) {
                                log.error("Ошибка выполнения запроса: " + e.getMessage());
                            }
                        }
                    );

                } catch (Exception e) {
                    log.info(e.getMessage());
                }
            }
        );
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
