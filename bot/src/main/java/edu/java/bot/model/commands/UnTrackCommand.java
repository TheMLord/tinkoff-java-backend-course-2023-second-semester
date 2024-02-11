package edu.java.bot.model.commands;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.model.SessionState;
import edu.java.bot.model.db_entities.User;
import edu.java.bot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Class untrack command.
 */
@Component("/untrack")
@Qualifier("action_command")
public final class UnTrackCommand implements Command {
    public static final String UNTRACK_MESSAGE = "укажите ссылку на ресурс, который больше не хотите отслеживать";
    public static final String UNKNOWN_USER = "Необходимо зарегистрироваться чтобы удалять отслеживаемые ссылки";
    private final UserRepository userRepository;

    public UnTrackCommand(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String nameCommand() {
        return "/untrack";
    }

    @Override
    public String description() {
        return "прекратить отслеживание ссылки";
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
    private String prepareUnTrackMessage(Long chatId) {
        var userOptional = userRepository.findUserById(chatId);

        if (userOptional.isPresent()) {
            var user = userOptional.get();
            changeStatusUserAndSave(user);
            return UNTRACK_MESSAGE;
        }
        return UNKNOWN_USER;
    }

    /**
     * method that changes the user's state to waiting for the monitored site to be deleted from the list
     */
    private void changeStatusUserAndSave(User user) {
        user.setState(SessionState.WAIT_URI_FOR_UNTRACKING);
        userRepository.saveUser(user);
    }
}
