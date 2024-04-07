package edu.java.bot.proxy;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.pengrad.telegrambot.TelegramBot;
import java.time.Duration;
import org.apache.kafka.clients.admin.AdminClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import reactor.test.StepVerifier;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static edu.java.bot.configuration.retry.BackOffPolicy.LINEAR;

@SpringBootTest
@DirtiesContext
@WireMockTest(httpPort = 8090)
public class LinearRetryTest {
    @MockBean AdminClient adminClient;
    @MockBean KafkaAdmin kafkaAdmin;

    @Autowired ScrapperProxy scrapperProxy;
    @MockBean TelegramBot telegramBot;

    @Test
    @DisplayName(
        "Test that the scraper client makes a repeat request and does it the expected number of times with linear repetition")
    void testThatTheScraperClientMakesARepeatRequestAndDoesItTheExpectedNumberOfTimesWithLinearRepetition() {
        stubFor(post(urlPathMatching("/tg-chat/.*"))
            .willReturn(aResponse()
                .withStatus(406)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                      "description": "Чат уже зарегистрирован",
                      "code": "406",
                      "exceptionName": "DoubleRegistrationException",
                      "exceptionMessage": "Чат уже зарегистрирован",
                      "stacktrace": [
                        "string"
                      ]
                    }""")
            ));

        StepVerifier.create(scrapperProxy.registerChat(1L))
            .expectSubscription()
            .expectNoEvent(Duration.ofSeconds(1))
            .expectNoEvent(Duration.ofSeconds(2))
            .expectNoEvent(Duration.ofSeconds(3))
            .expectNoEvent(Duration.ofSeconds(4))
            .expectError()
            .verify();
    }

    @DynamicPropertySource
    private static void setScrapperProxyProperty(DynamicPropertyRegistry registry) {
        registry.add("app.scrapper-base-uri", () -> "http://localhost:8090");
        registry.add("app.retry.back-off-policy", () -> LINEAR);
        registry.add("app.retry.httpStatuses", () -> HttpStatus.NOT_ACCEPTABLE);
        registry.add("app.retry.max-attempts", () -> 4);
        registry.add("app.retry.delay", () -> "1s");
    }

}
