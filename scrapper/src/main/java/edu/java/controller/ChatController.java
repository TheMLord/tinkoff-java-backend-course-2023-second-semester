package edu.java.controller;

import edu.java.services.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController implements TgChatApi {
    private final ChatService chatService;

    @Override
    public ResponseEntity<Void> tgChatIdDelete(Long id) {
        chatService.deleteUser(id);
        return ResponseEntity
            .ok()
            .build();

    }

    @Override
    public ResponseEntity<Void> tgChatIdPost(Long id) {
        chatService.registerUser(id);
        return ResponseEntity
            .ok()
            .build();
    }
}
