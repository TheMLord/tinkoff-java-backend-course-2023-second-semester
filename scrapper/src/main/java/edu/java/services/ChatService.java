package edu.java.services;

/**
 * Interface of the chat service.
 * <p>
 * Describes the contract for managing requests related to chats that come to controllers.
 */
public interface ChatService {
    /**
     * Method that registers the chat in the application
     *
     * @param chatId id chat.
     */
    void register(long chatId);

    /**
     * Method that removes the chat from the application.
     *
     * @param chatId id chat.
     */
    void unRegister(long chatId);
}
