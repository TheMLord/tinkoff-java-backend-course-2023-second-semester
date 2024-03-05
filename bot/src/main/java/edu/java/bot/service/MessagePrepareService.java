package edu.java.bot.service;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.models.commands.Command;
import edu.java.bot.models.db_entities.SessionState;
import edu.java.bot.models.db_entities.User;
import edu.java.bot.models.dto.api.request.AddLinkRequest;
import edu.java.bot.models.dto.api.request.RemoveLinkRequest;
import edu.java.bot.processor.UrlProcessor;
import edu.java.bot.proxy.ScrapperProxy;
import edu.java.bot.repository.UserRepository;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * Bots message service
 */
@Service
@RequiredArgsConstructor
public class MessagePrepareService {
    public static final String DO_REGISTRATION_MESSAGE = "Необходимо зарегистрироваться";
    public static final String INVALID_URI_MESSAGE = "Неверно указан URI";
    public static final String INVALID_COMMAND_MESSAGE = "Некорректная команда";
    public static final String SUCCESS_TRACK_SITE_MESSAGE = "Сайт успешно добавлен в отслеживание";
    public static final String DUPLICATE_TRACKING_MESSAGE = "Этот сайт уже отслеживается";
    public static final String INVALID_FOR_TRACK_SITE_MESSAGE = "Отслеживание ресурса с этого сайта не поддерживается";
    public static final String SUCCESS_UNTRACKING_SITE_MESSAGE = "Ресурс успешно удален из отслеживания";
    public static final String UNSUCCESSFUL_UNTRACKING_SITE_MESSAGE = "Вы не отслеживаете этот ресурс";
    private static final String SERVER_ERROR_MESSAGE = "Ошибка сервера";
    private static final String UNKNOWN_ERROR_MESSAGE = "Неизвестная ошибка";

    private static final String HTTP_PREFIX = "http";

    private final Map<String, Command> commandMap;
    private final UserRepository userRepository;
    private final UrlProcessor urlProcessor;
    private final ScrapperProxy scrapperProxy;

    /**
     * Method update processing and generating a response to the user.
     */
    public Mono<String> prepareResponseMessage(Update update) {
        var chatId = update.message().chat().id();
        var textMessage = update.message().text();

        var botCommand = commandMap.get(textMessage);
        return (botCommand != null) ? botCommand.execute(update)
            : processNonCommandMessage(chatId, textMessage);
    }

    /**
     * Method that generates a response to a message that does not contain a bot command
     */
    private Mono<String> processNonCommandMessage(Long chatId, String text) {
        return userRepository.findUserById(chatId).map(user -> {
                try {
                    if (!text.startsWith(HTTP_PREFIX)) {
                        return Mono.just(INVALID_COMMAND_MESSAGE);
                    }
                    return processStateUserMessage(
                        user,
                        new URI(text)
                    );
                } catch (URISyntaxException e) {
                    return Mono.just(INVALID_URI_MESSAGE);
                }
            }
        ).orElse(Mono.just(DO_REGISTRATION_MESSAGE));
    }

    /**
     * Method that handles the case of waiting for a link from the user
     */
    private Mono<String> processStateUserMessage(User user, URI uri) {
        if (user.isWaitingTrack()) {
            return prepareWaitTrackingMessage(user, uri);
        }

        if (user.isWaitingUntrack()) {
            return prepareWaitUnTrackingMessage(user, uri);
        }
        return Mono.just(INVALID_COMMAND_MESSAGE);
    }

    /**
     * Method reply to the user with the WAIT_URI_FOR_TRACKING status to the message.
     */
    private Mono<String> prepareWaitTrackingMessage(User user, URI uri) {
        if (urlProcessor.isValidUrl(uri)) {
            return scrapperProxy.addLink(new AddLinkRequest(uri), user.getId()).map(response -> {
                    resetUserState(user);
                    return SUCCESS_TRACK_SITE_MESSAGE;
                }
            ).onErrorResume(throwable -> {
                if (throwable instanceof WebClientResponseException exception) {
                    if (exception.getStatusCode().isSameCodeAs(HttpStatus.BAD_REQUEST)) {
                        return Mono.just(SERVER_ERROR_MESSAGE);
                    }
                    if (exception.getStatusCode().isSameCodeAs(HttpStatus.NOT_FOUND)) {
                        return Mono.just(DUPLICATE_TRACKING_MESSAGE);
                    }
                }
                return Mono.just(UNKNOWN_ERROR_MESSAGE);
            });
        }
        return Mono.just(INVALID_FOR_TRACK_SITE_MESSAGE);
    }

    /**
     * Method reply to the user with the WAIT_URI_FOR_UNTRACKING status to the message.
     */
    private Mono<String> prepareWaitUnTrackingMessage(User user, URI url) {
        if (urlProcessor.isValidUrl(url)) {
            return scrapperProxy.deleteLink(new RemoveLinkRequest(url), user.getId()).map(response -> {
                resetUserState(user);
                return SUCCESS_UNTRACKING_SITE_MESSAGE;
            }).onErrorResume(throwable -> {
                if (throwable instanceof WebClientResponseException exception) {
                    if (exception.getStatusCode().isSameCodeAs(HttpStatus.BAD_REQUEST)) {
                        return Mono.just(SERVER_ERROR_MESSAGE);
                    }
                    if (exception.getStatusCode().isSameCodeAs(HttpStatus.NOT_FOUND)) {
                        return Mono.just(UNSUCCESSFUL_UNTRACKING_SITE_MESSAGE);
                    }
                }
                return Mono.just(UNKNOWN_ERROR_MESSAGE);
            });

        }
        return Mono.just(INVALID_FOR_TRACK_SITE_MESSAGE);
    }

    /**
     * Method that makes commit to the user in the database
     */
    private void resetUserState(User user) {
        user.setState(SessionState.BASE_STATE);
        userRepository.saveUser(user);
    }
}
