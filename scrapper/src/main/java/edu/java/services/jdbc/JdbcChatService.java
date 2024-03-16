package edu.java.services.jdbc;

import edu.java.repository.TgChatRepository;
import edu.java.services.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the jdbc chat service.
 */
@Service
@RequiredArgsConstructor
public class JdbcChatService implements ChatService {
    private final TgChatRepository tgChatRepository;

    @Override
    @Transactional
    public void register(long chatId) {
        tgChatRepository.add(chatId);
    }

    @Override
    @Transactional
    public void unRegister(long chatId) {
        tgChatRepository.remove(chatId);
    }
}
