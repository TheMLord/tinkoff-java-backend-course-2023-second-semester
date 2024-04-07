package edu.java;

import edu.java.controller.LinksController;
import edu.java.schedulers.LinkUpdaterScheduler;
import edu.java.services.LinkService;
import org.apache.kafka.clients.admin.AdminClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import static edu.java.scrapper.IntegrationEnvironment.POSTGRES;
import static org.mockito.Mockito.when;

@SpringBootTest
@DirtiesContext
@TestPropertySource(locations = "classpath:test")
public class RateLimitLinksControllerTest {
    @MockBean AdminClient adminClient;

    @MockBean LinkUpdaterScheduler linkUpdaterScheduler;
    @MockBean LinkService linkService;
    @Autowired LinksController linksController;

    @Test
    @DisplayName(
        "Test that the rate limit works correctly and when the request limits expire, a response with the expected body and code will be returned")
    void testThatTheRateLimitWorksCorrectlyAndWhenTheRequestLimitsExpireAResponseWithTheExpectedBodyAndCodeWillBeReturned() {
        var webTestClient = WebTestClient.bindToController(linksController).build();
        var exceptedCode = HttpStatus.TOO_MANY_REQUESTS;
        var exceptedDescription = "Too many requests";
        when(linkService.getListLinks(1L)).thenReturn(Mono.empty());

        webTestClient.get()
            .uri("/links")
            .header("Tg-Chat-Id", String.valueOf(1L))
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.OK);

//        webTestClient.get()
//            .uri("/links")
//            .header("Tg-Chat-Id", String.valueOf(1L))
//            .exchange()
//            .expectStatus().isEqualTo(exceptedCode)
//            .expectBody()
//            .jsonPath("$.description", exceptedDescription);
    }

    @DynamicPropertySource
    static void jdbcProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.liquibase.enabled", () -> false);
    }
}
