package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.domain.TgChat;
import edu.java.bot.models.SessionState;
import edu.java.bot.repository.TgChatRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Class untrack command.
 */
@Component("/untrack")
@AllArgsConstructor
public final class UnTrackCommand implements Command {
    public static final String UNTRACK_MESSAGE = "укажите ссылку на ресурс, который больше не хотите отслеживать";
    public static final String UNKNOWN_USER = "Чат не зарегистрирован";

    private static final String NAME_COMMAND = "/untrack";
    private static final String DESCRIPTION_COMMAND = "прекратить отслеживание ссылки";

    private final TgChatRepository tgChatRepository;

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

        return Mono.just(prepareUnTrackMessage(chatId));
    }

    /**
     * Method that prepares the response to the execution of the untrack command
     *
     * @param chatId user id.
     */
    private String prepareUnTrackMessage(long chatId) {
        return tgChatRepository.findTgChatById(chatId).map(tgChat -> {
            changeStatusUserAndSave(tgChat);
            return UNTRACK_MESSAGE;
        }).orElse(UNKNOWN_USER);
    }

    /**
     * method that changes the user's state to waiting for the monitored site to be deleted from the list
     */
    private void changeStatusUserAndSave(TgChat tgChat) {
        tgChat.setState(SessionState.WAIT_URI_FOR_UNTRACKING);
        tgChatRepository.saveTgChat(tgChat);
    }
}
