package edu.java.bot.model.commands;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.proxy.ScrapperProxy;
import edu.java.bot.repository.UserRepository;
import java.net.URI;
import java.util.List;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

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

    private final ScrapperProxy scrapperProxy;
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
    public Mono<String> execute(Update update) {
        var chatId = update.message().chat().id();

        return userRepository.findUserById(chatId).map(
                user -> scrapperProxy.getListLinks()
                    .map(listLinksResponse -> {
                        var userList = listLinksResponse.getLinks()
                            .stream()
                            .filter(linkResponse -> linkResponse.getId().equals(chatId))
                            .flatMap(linkResponse -> Stream.of(linkResponse.getUrl())).toList();

                        return prepareListSitesMessage(userList);
                    })
                    .onErrorReturn("Ошибка сервера"))
            .orElse(Mono.just(UNKNOWN_USER));
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
