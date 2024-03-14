package edu.java.repository;

import edu.java.models.entities.TgChat;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

public interface TgChatRepository {
    @Transactional
    void add(Long chatId);

    Optional<TgChat> findById(Long chatId);

    @Transactional
    void remove(Long chatId);
}
