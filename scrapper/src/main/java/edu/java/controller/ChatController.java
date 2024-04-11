package edu.java.controller;

import edu.java.services.ChatService;
import io.micrometer.core.instrument.Counter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class ChatController implements TgChatApi {
    private final ChatService chatService;
    private final Counter processMessageMetric;

    @Override
    public Mono<ResponseEntity<Void>> tgChatIdDelete(Long id) {
        processMessageMetric.increment();
        return chatService.unRegister(id)
            .then(Mono.fromCallable(() ->
                ResponseEntity
                    .ok()
                    .build()
            ));
    }

    @SneakyThrows @Override
    public Mono<ResponseEntity<Void>> tgChatIdPost(Long id) {
        processMessageMetric.increment();
        return chatService.register(id).then(Mono.fromCallable(() ->
            ResponseEntity
                .ok()
                .build()
        ));
    }
}
