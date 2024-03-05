package edu.java.bot.service;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.model.SessionState;
import edu.java.bot.model.commands.Command;
import edu.java.bot.model.db_entities.User;
import edu.java.bot.processor.UrlProcessor;
import edu.java.bot.repository.UserRepository;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Bots message service
 */
@Service
@AllArgsConstructor
public class MessagePrepareService {
    public static final String DO_REGISTRATION_MESSAGE = "Необходимо зарегистрироваться";
    public static final String INVALID_UTI_MESSAGE = "Неверно указан URI";
    public static final String INVALID_COMMAND_MESSAGE = "Некорректная команда";
    public static final String SUCCESS_TRACK_SITE_MESSAGE = "Сайт успешно добавлен в отслеживание";
    public static final String DUPLICATE_TRACKING_MESSAGE = "Этот сайт уже отслеживается";
    public static final String INVALID_FOR_TRACK_SITE_MESSAGE = "Отслеживание ресурса с этого сайта не поддерживается";
    public static final String SUCCESS_UNTRACKING_SITE_MESSAGE = "Ресурс успешно удален из отслеживания";
    public static final String UNSUCCESSFUL_UNTRACKING_SITE_MESSAGE = "Вы не отслеживаете этот ресурс";

    private final Map<String, Command> commandMap;
    private final UserRepository userRepository;
    private final UrlProcessor urlProcessor;

    /**
     * Method update processing and generating a response to the user.
     */
    public Mono<String> prepareResponseMessage(Update update) {
        var chatId = update.message().chat().id();
        var textMessage = update.message().text();

        var botCommand = commandMap.get(textMessage);
        return (botCommand != null) ? botCommand.execute(update) :
            Mono.just(processNonCommandMessage(chatId, textMessage));
    }

    /**
     * Method that generates a response to a message that does not contain a bot command
     */
    private String processNonCommandMessage(Long chatId, String text) {
        return userRepository.findUserById(chatId).map(user -> {
                try {
                    if (!text.startsWith("http")) {
                        return INVALID_COMMAND_MESSAGE;
                    }

                    return processStateUserMessage(
                        user,
                        new URI(text)
                    );
                } catch (URISyntaxException e) {
                    return INVALID_UTI_MESSAGE;
                }
            }
        ).orElse(DO_REGISTRATION_MESSAGE);
    }

    /**
     * Method that handles the case of waiting for a link from the user
     */
    private String processStateUserMessage(User user, URI uri) {
        if (user.isWaitingTrack()) {
            return prepareWaitTrackingMessage(user, uri);
        }

        if (user.isWaitingUntrack()) {
            return prepareWaitUnTrackingMessage(user, uri);
        }
        return INVALID_COMMAND_MESSAGE;
    }

    /**
     * Method reply to the user with the WAIT_URI_FOR_TRACKING status to the message.
     */
    private String prepareWaitTrackingMessage(User user, URI url) {
        if (urlProcessor.isValidUrl(url)) {
            return (updateUserTrackingSites(user, url)) ? SUCCESS_TRACK_SITE_MESSAGE
                : DUPLICATE_TRACKING_MESSAGE;

        }
        return INVALID_FOR_TRACK_SITE_MESSAGE;
    }

    /**
     * Method reply to the user with the WAIT_URI_FOR_UNTRACKING status to the message.
     */
    private String prepareWaitUnTrackingMessage(User user, URI url) {
        if (urlProcessor.isValidUrl(url)) {
            return (deleteTrackingSites(user, url)) ? SUCCESS_UNTRACKING_SITE_MESSAGE
                : UNSUCCESSFUL_UNTRACKING_SITE_MESSAGE;
        }
        return INVALID_FOR_TRACK_SITE_MESSAGE;
    }

    /**
     * Method that changes the user by adding the site to the tracking
     *
     * @return true if it possible and false in another case.
     */
    private boolean updateUserTrackingSites(User user, URI uri) {
        List<URI> trackSites = new ArrayList<>(user.getSites());
        if (trackSites.contains(uri)) {
            return false;
        }
        trackSites.add(uri);
        updateTrackSitesAndCommit(user, trackSites);
        return true;
    }

    /**
     * Method that changes the user by removing the site from tracking
     *
     * @return true if it possible and false in another case.
     */
    private boolean deleteTrackingSites(User user, URI uri) {
        List<URI> trackSites = new ArrayList<>(user.getSites());
        if (!trackSites.contains(uri)) {
            return false;
        }

        trackSites.remove(uri);
        updateTrackSitesAndCommit(user, trackSites);
        return true;
    }

    /**
     * Method that makes commit to the user in the database
     */
    private void updateTrackSitesAndCommit(User user, List<URI> trackSites) {
        user.setSites(trackSites);
        user.setState(SessionState.BASE_STATE);
        userRepository.saveUser(user);
    }
}