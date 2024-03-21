package edu.java.scrapper.controller;

import edu.java.controller.ChatController;
import edu.java.controller.advice.ExceptionScrapperControllerAdvice;
import edu.java.exceptions.DoubleRegistrationException;
import edu.java.exceptions.NotExistTgChatException;
import edu.java.services.ChatService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@WebFluxTest(ChatController.class)
public class ChatControllerTest {
    @MockBean ChatService chatService;
    @Autowired WebTestClient webTestClient;

    @Test
    @DisplayName(
        "Test that the controller will return an ApiErrorResponse with the correct error and description to the user registration request that already exists")
    void testThatTheControllerWillReturnAnApiErrorResponseWithTheCorrectErrorAndDescriptionToTheUserRegistrationRequestThatAlreadyExists() {
        var exceptedDescription = ExceptionScrapperControllerAdvice.CHAT_ALREADY_REGISTER_DESCRIPTION;
        var exceptedCode = 406;
        var exceptedExceptionName = DoubleRegistrationException.class.getName();

        doThrow(new DoubleRegistrationException()).when(chatService).register(1L);
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
        doNothing().when(chatService).register(1L);

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

        doThrow(new NotExistTgChatException()).when(chatService).unRegister(1L);

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
        doNothing().when(chatService).unRegister(1L);

        webTestClient.delete().uri("/tg-chat/1")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.OK);
    }

}

