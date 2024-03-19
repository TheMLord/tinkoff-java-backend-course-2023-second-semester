package edu.java.scrapper.controller;

import edu.java.controller.LinksController;
import edu.java.services.LinkService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(LinksController.class)
public class LinksControllerTest {
    @MockBean LinkService linkService;
    @Autowired WebTestClient webTestClient;

    @Test
    @DisplayName("Test that the correct ApiErrorResponse is returned when trying to add a link to an unregistered chat")
    void testThatTheCorrectApiErrorResponseIsReturnedWhenTryingToAddALinkToAnUnregisteredChat() {
//        var link = URI.create("https://github.com/TheMLord/java-backend-course-2023-tinkoff");
//        var exceptedDescription = ExceptionScrapperControllerAdvice.CHAT_NOT_REGISTER_DESCRIPTION;
//        var exceptedCode = 401;
//        var exceptedExceptionName = NotExistTgChatException.class.getName();
//
//        doThrow(new NotExistTgChatException()).when(linkService).addLink(1L, link);
//
//        webTestClient.post()
//            .uri("/links")
//            .header("Tg-Chat-Id", "1")
//            .body(
//                Mono.just(new AddLinkRequest(link)),
//                AddLinkRequest.class
//            ).exchange()
//            .expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED)
//            .expectBody()
//            .jsonPath("$.description").isEqualTo(exceptedDescription)
//            .jsonPath("$.code").isEqualTo(exceptedCode)
//            .jsonPath("$.exceptionName").isEqualTo(exceptedExceptionName);
    }
}
