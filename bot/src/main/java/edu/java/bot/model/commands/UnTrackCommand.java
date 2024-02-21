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
 * Class untrack command.
 */
@Component("/untrack")
@AllArgsConstructor
public final class UnTrackCommand implements Command {
    public static final String UNTRACK_MESSAGE = "укажите ссылку на ресурс, который больше не хотите отслеживать";
    public static final String UNKNOWN_USER = "Необходимо зарегистрироваться чтобы удалять отслеживаемые ссылки";

    private static final String NAME_COMMAND = "/untrack";
    private static final String DESCRIPTION_COMMAND = "прекратить отслеживание ссылки";

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

        return prepareUnTrackMessage(chatId);
    }

    /**
     * Method that prepares the response to the execution of the untrack command
     *
     * @param chatId user id.
     */
    private String prepareUnTrackMessage(long chatId) {
        try {
            changeStatusUserAndSave(userRepository.findUserById(chatId).orElseThrow());
            return UNTRACK_MESSAGE;
        } catch (NoSuchElementException e) {
            return UNKNOWN_USER;
        }
    }

    /**
     * method that changes the user's state to waiting for the monitored site to be deleted from the list
     */
    private void changeStatusUserAndSave(User user) {
        user.setState(SessionState.WAIT_URI_FOR_UNTRACKING);
        userRepository.saveUser(user);
    }
}
