package edu.java.services.InMerory;

import edu.java.exceptions.DoubleRegistrationException;
import edu.java.exceptions.NotExistTgChatException;
import edu.java.models.TgChat;
import edu.java.repository.InMemoryChatRepository;
import edu.java.services.ChatService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class InMemoryChatService implements ChatService {
    private final InMemoryChatRepository chatRepository;

    @Override
    public void register(long chatId) {
        log.info("регистрация чата {}", chatId);
        chatRepository.findUserById(chatId)
            .ifPresentOrElse(
                user -> {
                    throw new DoubleRegistrationException();
                },
                () -> chatRepository.saveUser(new TgChat(chatId, List.of()))
            );
    }

    @Override
    public void unRegister(long chatId) {
        log.info("удаление чата {}", chatId);
        chatRepository.findUserById(chatId)
            .ifPresentOrElse(
                user -> chatRepository.deleteByIdUser(chatId),
                () -> {
                    throw new NotExistTgChatException();
                }
            );
    }
}
