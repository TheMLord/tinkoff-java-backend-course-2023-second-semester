package edu.java.bot.repository;

import edu.java.bot.domain.TgChat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class TgChatRepository {
    private final Map<Long, TgChat> userDb = new HashMap<>();

    public Optional<TgChat> findTgChatById(Long id) {
        return (userDb.containsKey(id)) ? Optional.of(userDb.get(id)) : Optional.empty();
    }

    public void saveTgChat(TgChat tgChat) {
        userDb.put(tgChat.getId(), tgChat);
    }
}
