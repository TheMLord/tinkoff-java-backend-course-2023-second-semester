package edu.java.services.jdbc;

import edu.java.repository.TgChatRepository;
import edu.java.services.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of the jdbc chat service.
 */
@Service
@RequiredArgsConstructor
public class JdbcChatService implements ChatService {
    private final TgChatRepository tgChatRepository;

    @Override
    public void register(long chatId) {
        tgChatRepository.add(chatId);
    }

    @Override
    public void unRegister(long chatId) {
        tgChatRepository.remove(chatId);
    }
}
