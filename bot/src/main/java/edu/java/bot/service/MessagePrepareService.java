package edu.java.bot.service;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.models.commands.Command;
import edu.java.bot.models.db_entities.SessionState;
import edu.java.bot.models.db_entities.User;
import edu.java.bot.models.dto.api.request.AddLinkRequest;
import edu.java.bot.models.dto.api.request.RemoveLinkRequest;
import edu.java.bot.models.dto.api.response.ApiErrorResponse;
import edu.java.bot.models.dto.api.response.LinkResponse;
import edu.java.bot.processor.UrlProcessor;
import edu.java.bot.proxy.ScrapperProxy;
import edu.java.bot.repository.UserRepository;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
        return (botCommand != null) ? botCommand.execute(update) :
            processNonCommandMessage(chatId, textMessage);
    }

    /**
     * Method that generates a response to a message that does not contain a bot command
     */
    private Mono<String> processNonCommandMessage(Long chatId, String text) {
        return userRepository.findUserById(chatId).map(user -> {
                try {
                    if (!text.startsWith("http")) {
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
    private Mono<String> prepareWaitTrackingMessage(User user, URI url) {
        if (urlProcessor.isValidUrl(url)) {
            return updateUserTrackingSites(user, url).map(success ->
                success ? SUCCESS_TRACK_SITE_MESSAGE : DUPLICATE_TRACKING_MESSAGE);
        }
        return Mono.just(INVALID_FOR_TRACK_SITE_MESSAGE);
    }

    /**
     * Method reply to the user with the WAIT_URI_FOR_UNTRACKING status to the message.
     */
    private Mono<String> prepareWaitUnTrackingMessage(User user, URI url) {
        if (urlProcessor.isValidUrl(url)) {
            return (deleteTrackingSites(user, url)).map(success ->
                success ? SUCCESS_UNTRACKING_SITE_MESSAGE : UNSUCCESSFUL_UNTRACKING_SITE_MESSAGE);
        }
        return Mono.just(INVALID_FOR_TRACK_SITE_MESSAGE);
    }

    private Mono<Boolean> updateUserTrackingSites(User user, URI uri) {
        return scrapperProxy.addLink(new AddLinkRequest(uri), user.getId()).map(response -> {
            if (response instanceof LinkResponse) {
                updateTrackSitesAndCommit(user);
                return true;
            }
            var errorResponse = (ApiErrorResponse) response;
            return false;
        });
    }

    private Mono<Boolean> deleteTrackingSites(User user, URI uri) {
        return scrapperProxy.deleteLink(new RemoveLinkRequest(uri), user.getId()).map(response -> {
            if (response instanceof LinkResponse) {
                updateTrackSitesAndCommit(user);
                return true;
            }
            var errorResponse = (ApiErrorResponse) response;
            return false;
        });
    }

    /**
     * Method that makes commit to the user in the database
     */
    private void updateTrackSitesAndCommit(User user) {
        user.setState(SessionState.BASE_STATE);
        userRepository.saveUser(user);
    }
}
