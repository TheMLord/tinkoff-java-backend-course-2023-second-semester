package edu.java.bot.models.commands;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.models.db_entities.SessionState;
import edu.java.bot.models.db_entities.User;
import edu.java.bot.proxy.ScrapperProxy;
import edu.java.bot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * Class start command
 */
@Component("/start")
@Slf4j
@RequiredArgsConstructor
public final class StartCommand implements Command {
    public static final String REGISTRATION_MESSAGE_SUCCESS = "Вы успешно зарегистрировались!";
    public static final String ALREADY_EXIST_MESSAGE = "Вы уже зарегистрированы";

    private static final String NAME_COMMAND = "/start";
    private static final String DESCRIPTION_COMMAND = "зарегистрировать пользователя";

    private final UserRepository userRepository;
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
        return userRepository.findUserById(chatId)
            .map(user -> Mono.just(ALREADY_EXIST_MESSAGE))
            .orElseGet(() ->
                scrapperProxy.registerChat(chatId)
                    .then(Mono.fromCallable(() -> {
                        userRepository.saveUser(new User(chatId, SessionState.BASE_STATE));
                        return REGISTRATION_MESSAGE_SUCCESS;
                    }))
                    .onErrorResume(throwable -> {
                        if (throwable instanceof WebClientResponseException exception) {
                            if (exception.getStatusCode().isSameCodeAs(HttpStatus.BAD_REQUEST)) {
                                log.info("Некорректные параметры запроса");
                                return Mono.just("Ошибка сервера");
                            }
                            if (exception.getStatusCode().isSameCodeAs(HttpStatus.NOT_FOUND)) {
                                return Mono.just("Чат уже зарегистрирован");
                            }
                        }
                        return Mono.just("Неизвестная ошибка");
                    })
            );
    }
}
