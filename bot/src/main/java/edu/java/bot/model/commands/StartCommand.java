package edu.java.bot.model.commands;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.model.SessionState;
import edu.java.bot.model.db_entities.User;
import edu.java.bot.repository.UserRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Class start command
 */
@Component("/start")
@AllArgsConstructor
public final class StartCommand implements Command {
    public static final String REGISTRATION_MESSAGE_SUCCESS = "Вы успешно зарегистрировались!";
    public static final String ALREADY_EXIST_MESSAGE = "Вы уже зарегистрированный";

    private static final String NAME_COMMAND = "/start";
    private static final String DESCRIPTION_COMMAND = "зарегистрировать пользователя";

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

        return registerUser(chatId);
    }

    /**
     * Method that performs user registration
     *
     * @param chatId user id.
     */
    private String registerUser(long chatId) {
        return userRepository.findUserById(chatId).map(user -> ALREADY_EXIST_MESSAGE)
            .orElseGet(() -> {
                userRepository.saveUser(new User(chatId, List.of(), SessionState.BASE_STATE));
                return REGISTRATION_MESSAGE_SUCCESS;
            });
    }
}
