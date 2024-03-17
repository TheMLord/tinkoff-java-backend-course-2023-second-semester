package edu.java.repository;

import edu.java.domain.jooq.tables.pojos.Tgchat;
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
    void add(Long chatId);

    /**
     * Method searching tg chat entity by id.
     *
     * @param chatId id chat to search.
     * @return if the chat finds it, it returns the chat entity otherwise empty Optional.
     */
    Optional<Tgchat> findById(Long chatId);

    /**
     * Method for deleting a chat from the tgChat table.
     *
     * @param chatId id chat to removing.
     */
    void remove(Long chatId);
}
