package edu.java.repository;

import edu.java.domain.jooq.pojos.Tgchats;
import java.util.Optional;
import reactor.core.publisher.Mono;

/**
 * contract tg chat repository.
 */
public interface TgChatRepository {
    /**
     * Method for adding a new chat to the tgChat table.
     *
     * @param chatId id chat to add.
     */
    Mono<Void> add(Long chatId);

    /**
     * Method searching tg chat entity by id.
     *
     * @param chatId id chat to search.
     * @return if the chat finds it, it returns the chat entity otherwise empty Optional.
     */

    Mono<Optional<Tgchats>> findById(Long chatId);

    /**
     * Method for deleting a chat from the tgChat table.
     *
     * @param chatId id chat to removing.
     */
    Mono<Void> remove(Long chatId);
}
