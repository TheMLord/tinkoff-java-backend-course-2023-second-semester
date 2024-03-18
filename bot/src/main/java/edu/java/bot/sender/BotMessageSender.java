package edu.java.bot.sender;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import edu.java.bot.exceptions.InvalidUpdateException;
import edu.java.bot.models.dto.TelegramMessage;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@Slf4j
@RequiredArgsConstructor
public class BotMessageSender {
    private final TelegramBot telegramBot;

    public void sendMessage(Flux<TelegramMessage> messageFlux) {
        messageFlux.subscribe(message -> {
            telegramBot.execute(
                new SendMessage(message.chatId(), message.message()),
                new Callback<SendMessage, SendResponse>() {
                    @Override
                    public void onResponse(SendMessage sendMessage, SendResponse sendResponse) {
                        log.info("Отправка сообщения {} на чат {}", message.message(), message.chatId());
                    }

                    @Override
                    public void onFailure(SendMessage sendMessage, IOException e) {
                        log.error("Ошибка отправки сообщения: " + e.getMessage());
                        throw new InvalidUpdateException("Ошибка отправки сообщения %s чату %d ".formatted(
                            message.message(),
                            message.chatId()
                        ));
                    }
                }
            );
        });
    }
}
