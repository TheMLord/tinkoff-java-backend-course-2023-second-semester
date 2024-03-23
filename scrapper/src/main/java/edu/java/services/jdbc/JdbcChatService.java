package edu.java.services.jdbc;

import edu.java.repository.TgChatRepository;
import edu.java.services.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/**
 * Implementation of the jdbc chat service.
 */
@RequiredArgsConstructor
public class JdbcChatService implements ChatService {
    private final TgChatRepository tgChatRepository;

    @Override
    @Transactional
    public Mono<Void> register(long chatId) {
        return tgChatRepository.add(chatId);
    }

    @Override
    @Transactional
    public Mono<Void> unRegister(long chatId) {
        return tgChatRepository.remove(chatId);
    }
}
