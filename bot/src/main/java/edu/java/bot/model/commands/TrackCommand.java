package edu.java.bot.model.commands;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.model.SessionState;
import edu.java.bot.model.db_entities.User;
import edu.java.bot.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import java.util.NoSuchElementException;

/**
 * Class track command.
 */
@Component("/track")
@AllArgsConstructor
public final class TrackCommand implements Command {
    public static final String TRACK_MESSAGE = "укажите ссылку на интересующий ресурс";
    public static final String UNKNOWN_USER = "Необходимо зарегистрироваться чтобы отслеживать ссылки";

    private static final String NAME_COMMAND = "/track";
    private static final String DESCRIPTION_COMMAND = "начать отслеживание ссылки";

    private final UserRepository userRepository;

    @Override
    public String nameCommand() {
        return NAME_COMMAND;
    }

    @Override
    public String description() {
        return DESCRIPTION_COMMAND;
    }

    @Override
    public String execute(Update update) {
        var chatId = update.message().chat().id();

        return prepareTrackMessage(chatId);
    }

    /**
     * Method that prepares the response to the execution of the track command
     *
     * @param chatId user id.
     */
    private String prepareTrackMessage(long chatId) {
        try {
            changeStatusUserAndSave(userRepository.findUserById(chatId).orElseThrow());
            return TRACK_MESSAGE;
        } catch (NoSuchElementException e) {
            return UNKNOWN_USER;
        }
    }

    /**
     * Method that changes the user's state to waiting for the monitored site to be received
     */
    private void changeStatusUserAndSave(User user) {
        user.setState(SessionState.WAIT_URI_FOR_TRACKING);
        userRepository.saveUser(user);
    }
}
