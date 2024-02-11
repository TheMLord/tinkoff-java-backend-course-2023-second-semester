package edu.java.bot.model.commands;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.model.SessionState;
import edu.java.bot.model.db_entities.User;
import edu.java.bot.repository.UserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Class start command
 */
@Component("/start")
@Qualifier("action_command")
public final class StartCommand implements Command {
    public static final String REGISTRATION_MESSAGE_SUCCESS = "Вы успешно зарегистрировались!";
    public static final String ALREADY_EXIST_MESSAGE = "Вы уже зарегистрированный";
    private final UserRepository userRepository;

    public StartCommand(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String nameCommand() {
        return "/start";
    }

    @Override
    public String description() {
        return "зарегистрировать пользователя";
    }

    @Override
    public String execute(Update update) {
        var chatId = update.message().chat().id();

        return registerUser(chatId);
    }

    /**
     * Method that performs user registration
     *
     * @param chatId user id.
     */
    private String registerUser(long chatId) {
        var userOptional = userRepository.findUserById(chatId);

        if (userOptional.isEmpty()) {
            var user = new User(chatId, List.of(), SessionState.BASE_STATE);
            userRepository.saveUser(user);
            return REGISTRATION_MESSAGE_SUCCESS;
        }
        return ALREADY_EXIST_MESSAGE;
    }
}
