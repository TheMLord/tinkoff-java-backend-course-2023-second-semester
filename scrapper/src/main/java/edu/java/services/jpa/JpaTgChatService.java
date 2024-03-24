package edu.java.services.jpa;

import edu.java.domain.jpa.TgChats;
import edu.java.exceptions.DoubleRegistrationException;
import edu.java.exceptions.NotExistTgChatException;
import edu.java.repository.jpa.JpaTgChatRepository;
import edu.java.services.ChatService;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class JpaTgChatService implements ChatService {
    private final JpaTgChatRepository jpaTgChatRepository;

    @Override
    public Mono<Void> register(long chatId) {
        return Mono.defer(() -> {
            var optionalTgChats = jpaTgChatRepository.findById(chatId);
            if (optionalTgChats.isPresent()) {
                return Mono.error(new DoubleRegistrationException());
            } else {
                TgChats newChat = new TgChats();
                newChat.setId(chatId);
                newChat.setCreatedAt(OffsetDateTime.now());

                jpaTgChatRepository.saveAndFlush(newChat);
                return Mono.empty();
            }
        });
    }

    @Override
    public Mono<Void> unRegister(long chatId) {
        return Mono.defer(() -> {
            var optionalTgChats = jpaTgChatRepository.findById(chatId);
            if (optionalTgChats.isEmpty()) {
                return Mono.error(new NotExistTgChatException());
            } else {
                jpaTgChatRepository.delete(optionalTgChats.get());
                return Mono.empty();
            }
        });
    }
}
