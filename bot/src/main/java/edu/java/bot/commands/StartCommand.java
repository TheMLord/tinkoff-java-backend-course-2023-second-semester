package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.domain.TgChat;
import edu.java.bot.exceptions.ScrapperApiException;
import edu.java.bot.models.SessionState;
import edu.java.bot.proxy.ScrapperProxy;
import edu.java.bot.repository.TgChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Class start command
 */
@Component("/start")
@Slf4j
@RequiredArgsConstructor
public final class StartCommand implements Command {
    public static final String REGISTRATION_MESSAGE_SUCCESS = "Вы успешно зарегистрировались!";
    private static final String NAME_COMMAND = "/start";
    private static final String DESCRIPTION_COMMAND = "зарегистрировать пользователя";

    private final TgChatRepository tgChatRepository;
    private final ScrapperProxy scrapperProxy;

    @Override
    public String nameCommand() {
        return NAME_COMMAND;
    }

    @Override
    public String description() {
        return DESCRIPTION_COMMAND;
    }

    @Override
    public Mono<String> execute(Update update) {
        var chatId = update.message().chat().id();

        return registerUser(chatId);
    }

    /**
     * Method that performs user registration
     *
     * @param chatId user id.
     */
    private Mono<String> registerUser(long chatId) {
        return scrapperProxy.registerChat(chatId)
            .then(Mono.fromCallable(() -> {
                tgChatRepository.saveTgChat(new TgChat(chatId, SessionState.BASE_STATE));
                return REGISTRATION_MESSAGE_SUCCESS;
            }))
            .onErrorResume(throwable -> {
                if (throwable instanceof ScrapperApiException exception) {
                    log.info(
                        "запрос на регистрацию вернулся с кодом {} и ошибкой {}",
                        exception.getApiErrorResponse().getCode(),
                        exception.getApiErrorResponse().getExceptionName()
                    );
                    return Mono.just(exception.getApiErrorResponse().getDescription());
                }
                log.error("неизвестная ошибка - {}", throwable.getMessage());
                return Mono.just("Неизвестная ошибка");
            });
    }
}
