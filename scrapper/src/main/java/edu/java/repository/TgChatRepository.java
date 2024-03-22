package edu.java.repository;

import edu.java.models.entities.TgChat;
import reactor.core.publisher.Mono;
import java.util.Optional;

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
    Mono<Optional<TgChat>> findById(Long chatId);

    /**
     * Method for deleting a chat from the tgChat table.
     *
     * @param chatId id chat to removing.
     */
    Mono<Void> remove(Long chatId);
}
