package edu.java.bot.model.commands;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.repository.UserRepository;
import java.net.URI;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Class list command.
 */

@Component("/list")
@AllArgsConstructor
public final class ListCommand implements Command {
    public static final String EMPTY_LIST_SITES = "Вы не отслеживаете ни одну ссылку";
    public static final String UNKNOWN_USER =
        "Необходимо зарегистрироваться для просмотра списка отслеживаемых ссылок";
    private static final String USER_TRACK_SITES_MESSAGE = "Вы отслеживаете %d сайтов\n";
    private static final String LIST_TRACK_SEPARATOR = "\n";
    private static final String NAME_COMMAND = "/list";
    private static final String DESCRIPTION_COMMAND = "команда показать список отслеживаемых ссылок";

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

        var userOptional = userRepository.findUserById(chatId);

        if (userOptional.isPresent()) {
            var listSites = userOptional.get().getSites();
            if (!listSites.isEmpty()) {
                return prepareListSitesMessage(listSites);
            }
            return EMPTY_LIST_SITES;
        }
        return UNKNOWN_USER;
    }

    /**
     * Method that prepares a message about the user's monitored sites.
     */
    private String prepareListSitesMessage(List<URI> uriList) {
        var sitesString = new StringBuilder();

        sitesString.append(USER_TRACK_SITES_MESSAGE.formatted(uriList.size()));
        for (URI uri : uriList) {
            sitesString.append(uri.toString()).append(LIST_TRACK_SEPARATOR);
        }

        return sitesString.toString();
    }
}
