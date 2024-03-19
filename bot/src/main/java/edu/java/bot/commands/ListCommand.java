package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.exceptions.ScrapperApiException;
import edu.java.bot.proxy.ScrapperProxy;
import edu.java.bot.repository.TgChatRepository;
import java.net.URI;
import java.util.List;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Class list command.
 */

@Component("/list")
@Slf4j
@RequiredArgsConstructor
public final class ListCommand implements Command {
    private static final String USER_TRACK_SITES_MESSAGE = "Вы отслеживаете %d сайтов\n";
    private static final String LIST_TRACK_SEPARATOR = "\n";
    private static final String NAME_COMMAND = "/list";
    private static final String DESCRIPTION_COMMAND = "команда показать список отслеживаемых ссылок";

    private final ScrapperProxy scrapperProxy;

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

        return scrapperProxy.getListLinks(chatId)
            .map(response -> {
                var userList = response.getLinks().stream()
                    .flatMap(linkResponse -> Stream.of(linkResponse.getUrl())).toList();
                return prepareListSitesMessage(userList);
            })
            .onErrorResume(throwable -> {
                if (throwable instanceof ScrapperApiException exception) {
                    log.info(
                        "запрос на регистрацию вернулся с кодом {} и ошибкой {}",
                        exception.getApiErrorResponse().getCode(),
                        exception.getApiErrorResponse().getExceptionName()
                    );
                    return Mono.just(exception.getApiErrorResponse().getDescription());
                }
                log.error("неизвестная ошибка - {}", throwable.getMessage());
                return Mono.just("Неизвестная ошибка");
            });
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
