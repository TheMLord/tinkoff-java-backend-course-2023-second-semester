package edu.java.bot.service;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.commands.Command;
import edu.java.bot.domain.TgChat;
import edu.java.bot.exceptions.ScrapperApiException;
import edu.java.bot.models.SessionState;
import edu.java.bot.models.dto.api.request.AddLinkRequest;
import edu.java.bot.models.dto.api.request.RemoveLinkRequest;
import edu.java.bot.processor.UrlProcessor;
import edu.java.bot.proxy.ScrapperProxy;
import edu.java.bot.repository.TgChatRepository;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Service for preparing messages for chats.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("MultipleStringLiterals")
public class MessageService {
    public static final String DO_REGISTRATION_MESSAGE = "Необходимо зарегистрироваться";
    public static final String INVALID_URI_MESSAGE = "Неверно указан URI";
    public static final String INVALID_COMMAND_MESSAGE = "Некорректная команда";
    public static final String SUCCESS_TRACK_SITE_MESSAGE = "Сайт успешно добавлен в отслеживание";
    public static final String INVALID_FOR_TRACK_SITE_MESSAGE = "Отслеживание ресурса с этого сайта не поддерживается";
    public static final String SUCCESS_UNTRACKING_SITE_MESSAGE = "Ресурс успешно удален из отслеживания";
    public static final String UNSUCCESSFUL_UNTRACKING_SITE_MESSAGE = "Ссылка не отслеживается чатом";
    private static final String UNKNOWN_ERROR_MESSAGE = "Неизвестная ошибка";

    private static final String HTTP_PREFIX = "http";

    private final Map<String, Command> commandMap;
    private final TgChatRepository tgChatRepository;
    private final UrlProcessor urlProcessor;
    private final ScrapperProxy scrapperProxy;

    /**
     * Method update processing and generating a response to the tgChat.
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
        return tgChatRepository.findTgChatById(chatId).map(tgChat -> {
                try {
                    if (!text.startsWith(HTTP_PREFIX)) {
                        return Mono.just(INVALID_COMMAND_MESSAGE);
                    }
                    return processStateTgChatMessage(
                        tgChat,
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
    private Mono<String> processStateTgChatMessage(TgChat tgChat, URI uri) {
        if (tgChat.isWaitingTrack()) {
            return prepareWaitTrackingMessage(tgChat, uri);
        }

        if (tgChat.isWaitingUntrack()) {
            return prepareWaitUnTrackingMessage(tgChat, uri);
        }
        return Mono.just(INVALID_COMMAND_MESSAGE);
    }

    /**
     * Method reply to the user with the WAIT_URI_FOR_TRACKING status to the message.
     */
    private Mono<String> prepareWaitTrackingMessage(TgChat tgChat, URI uri) {
        if (urlProcessor.isValidUrl(uri)) {
            return scrapperProxy.addLink(new AddLinkRequest(uri), tgChat.getId()).map(response -> {
                    resetUserState(tgChat);
                    return SUCCESS_TRACK_SITE_MESSAGE;
                }
            ).onErrorResume(throwable -> {
                if (throwable instanceof ScrapperApiException scrapperException) {
                    log.error(
                        "запрос на добавление ссылки вернулся с кодом {} и ошибкой {}",
                        scrapperException.getApiErrorResponse().getCode(),
                        scrapperException.getApiErrorResponse().getExceptionName()
                    );
                    return Mono.just(scrapperException.getApiErrorResponse().getDescription());
                }
                log.error("неизвестная ошибка - {}", throwable.getMessage());
                return Mono.just(UNKNOWN_ERROR_MESSAGE);
            });
        }
        return Mono.just(INVALID_FOR_TRACK_SITE_MESSAGE);
    }

    /**
     * Method reply to the user with the WAIT_URI_FOR_UNTRACKING status to the message.
     */
    private Mono<String> prepareWaitUnTrackingMessage(TgChat tgChat, URI url) {
        if (urlProcessor.isValidUrl(url)) {
            return scrapperProxy.deleteLink(new RemoveLinkRequest(url), tgChat.getId()).map(response -> {
                resetUserState(tgChat);
                return SUCCESS_UNTRACKING_SITE_MESSAGE;
            }).onErrorResume(throwable -> {
                if (throwable instanceof ScrapperApiException scrapperException) {
                    var httpCode = scrapperException.getApiErrorResponse().getCode();
                    log.error(
                        "запрос на добавление ссылки вернулся с кодом {} и ошибкой {}",
                        httpCode,
                        scrapperException.getApiErrorResponse().getExceptionName()
                    );
                    return httpCode.equals("403") || httpCode.equals("404")
                        ? Mono.just(UNSUCCESSFUL_UNTRACKING_SITE_MESSAGE)
                        : Mono.just(scrapperException.getApiErrorResponse().getDescription());
                }
                log.error("неизвестная ошибка - {}", throwable.getMessage());
                return Mono.just(UNKNOWN_ERROR_MESSAGE);
            });

        }
        return Mono.just(INVALID_FOR_TRACK_SITE_MESSAGE);
    }

    /**
     * Method that makes commit to the user in the database
     */
    private void resetUserState(TgChat tgChat) {
        tgChat.setState(SessionState.BASE_STATE);
        tgChatRepository.saveTgChat(tgChat);
    }
}
