package edu.java.controller;

import edu.java.services.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class ChatController implements TgChatApi {
    private final ChatService chatService;

    @Override

    public Mono<ResponseEntity<Void>> tgChatIdDelete(Long id) {
        chatService.unRegister(id);
        return Mono.just(
            ResponseEntity
                .ok()
                .build()
        );
    }

    @Override
    public Mono<ResponseEntity<Void>> tgChatIdPost(Long id) {
        chatService.register(id);
        return Mono.just(
            ResponseEntity
                .ok()
                .build()
        );
    }
}
