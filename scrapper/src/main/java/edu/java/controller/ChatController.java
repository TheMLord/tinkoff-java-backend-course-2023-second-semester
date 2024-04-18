package edu.java.controller;

import edu.java.servicies.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class ChatController implements TgChatApi {
    private final ChatService chatService;

    @Override
    public Mono<ResponseEntity<Void>> tgChatIdDelete(Long id) {
        return chatService.unRegister(id)
            .then(Mono.fromCallable(() ->
                ResponseEntity
                    .ok()
                    .build()
            ));
    }

    @SneakyThrows @Override
    public Mono<ResponseEntity<Void>> tgChatIdPost(Long id) {
        return chatService.register(id).then(Mono.fromCallable(() ->
            ResponseEntity
                .ok()
                .build()
        ));
    }
}
