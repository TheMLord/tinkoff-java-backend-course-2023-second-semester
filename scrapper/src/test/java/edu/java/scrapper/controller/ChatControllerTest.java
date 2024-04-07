package edu.java.scrapper.controller;

import edu.java.controller.ChatController;
import edu.java.controller.advice.ExceptionScrapperControllerAdvice;
import edu.java.exceptions.DoubleRegistrationException;
import edu.java.exceptions.NotExistTgChatException;
import edu.java.services.ChatService;
import org.apache.kafka.clients.admin.AdminClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import static org.mockito.Mockito.when;

@WebFluxTest(ChatController.class)
@DirtiesContext
public class ChatControllerTest {
    @MockBean AdminClient adminClient;

    @MockBean ChatService chatService;
    @Autowired WebTestClient webTestClient;

    @Test
    @DisplayName(
        "Test that the controller will return an ApiErrorResponse with the correct error and description to the user registration request that already exists")
    void testThatTheControllerWillReturnAnApiErrorResponseWithTheCorrectErrorAndDescriptionToTheUserRegistrationRequestThatAlreadyExists() {
        var exceptedDescription = ExceptionScrapperControllerAdvice.CHAT_ALREADY_REGISTER_DESCRIPTION;
        var exceptedCode = 406;
        var exceptedExceptionName = DoubleRegistrationException.class.getName();

        when(chatService.register(1L)).thenReturn(Mono.error(new DoubleRegistrationException()));
        webTestClient.post().uri("/tg-chat/1")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.NOT_ACCEPTABLE)
            .expectBody()
            .jsonPath("$.description").isEqualTo(exceptedDescription)
            .jsonPath("$.code").isEqualTo(exceptedCode)
            .jsonPath("$.exceptionName").isEqualTo(exceptedExceptionName)
            .returnResult();
    }

    @Test
    @DisplayName("Test that the ok status is returned upon a successful request to add a chat")
    void testThatTheOkStatusIsReturnedUponASuccessfulRequestToAddAChat() {
        when(chatService.register(1L)).thenReturn(Mono.empty());

        webTestClient.post().uri("/tg-chat/1")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Test that the correct ApiErrorResponse is returned when trying to delete a non-existent chat")
    void testThatTheCorrectApiErrorResponseIsReturnedWhenTryingToDeleteANonExistentChat() {
        var exceptedDescription = ExceptionScrapperControllerAdvice.CHAT_NOT_REGISTER_DESCRIPTION;
        var exceptedCode = 401;
        var exceptedExceptionName = NotExistTgChatException.class.getName();

        when(chatService.unRegister(1L)).thenReturn(Mono.error(new NotExistTgChatException()));

        webTestClient.delete().uri("/tg-chat/1")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED)
            .expectBody()
            .jsonPath("$.description").isEqualTo(exceptedDescription)
            .jsonPath("$.code").isEqualTo(exceptedCode)
            .jsonPath("$.exceptionName").isEqualTo(exceptedExceptionName)
            .returnResult();
    }

    @Test
    @DisplayName("Test that the Ok status is returned when the chat is successfully deleted")
    void testThatTheOkStatusIsReturnedWhenTheChatIsSuccessfullyDeleted() {
        when(chatService.unRegister(1L)).thenReturn(Mono.empty());
        webTestClient.delete().uri("/tg-chat/1")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.OK);
    }

}

