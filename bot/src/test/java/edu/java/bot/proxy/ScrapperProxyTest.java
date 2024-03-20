package edu.java.bot.proxy;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.bot.exceptions.ScrapperApiException;
import edu.java.bot.models.dto.api.request.AddLinkRequest;
import edu.java.bot.models.dto.api.request.RemoveLinkRequest;
import edu.java.bot.models.dto.api.response.LinkResponse;
import java.net.URI;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import reactor.test.StepVerifier;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@WireMockTest(httpPort = 8090)
class ScrapperProxyTest {
    @Autowired ScrapperProxy scrapperProxy;

    private static final String getAllLinksAnswer = """
        {
          "links": [
            {
              "id": 1,
              "url": "https://github.com/TheMLord/tinkoff-java-backend-course-2023-second-semester1"
            },
            {
              "id": 2,
              "url": "https://github.com/TheMLord/tinkoff-java-backend-course-2023-second-semester2"
            }
          ],
          "size": 2
        }""";
    private static final String addAndDeleteLinkAnswer = """
        {
           "id": 1,
           "url": "https://github.com/TheMLord/tinkoff-java-backend-course-2023-second-semester"
        }""";

    private static final String apiErrorResponseAnswer = """
        {
          "description": "Чат уже зарегистрирован",
          "code": "406",
          "exceptionName": "DoubleRegistrationException",
          "exceptionMessage": "Чат уже зарегистрирован",
          "stacktrace": [
            "string"
          ]
        }""";

    @Test
    @DisplayName(
        "Test that the response is correctly accepted if the link is successfully added and returned a response containing the expected arguments")
    void testThatTheResponseIsCorrectlyAcceptedIfTheLinkIsSuccessfullyAddedAndReturnedAResponseContainingTheExpectedArguments() {
        setUpServerPost("/links", addAndDeleteLinkAnswer);
        var exceptedLinkId = 1;
        var exceptedLinkUrl =
            URI.create("https://github.com/TheMLord/tinkoff-java-backend-course-2023-second-semester");

        var response = scrapperProxy.addLink(new AddLinkRequest(exceptedLinkUrl), 1L).block();

        assertThat(response.getId()).isEqualTo(exceptedLinkId);
        assertThat(response.getUrl()).isEqualTo(exceptedLinkUrl);
    }

    @Test
    @DisplayName(
        "Test that the response is correctly accepted in case of successful deletion of the link and returned a response containing the expected arguments")
    void testThatTheResponseIsCorrectlyAcceptedInCaseOfSuccessfulDeletionOfTheLinkAndReturnedAResponseContainingTheExpectedArguments() {
        setUpServerDelete("/links", addAndDeleteLinkAnswer);
        var exceptedLinkId = 1;
        var exceptedLinkUrl =
            URI.create("https://github.com/TheMLord/tinkoff-java-backend-course-2023-second-semester");

        var response = scrapperProxy.deleteLink(new RemoveLinkRequest(exceptedLinkUrl), 1L).block();

        assertThat(response.getId()).isEqualTo(exceptedLinkId);
        assertThat(response.getUrl()).isEqualTo(exceptedLinkUrl);
    }

    @Test
    @DisplayName(
        "Test that the response is correctly accepted in case of a successful request to receive all links and returned a response containing the expected arguments")
    void testThatTheResponseIsCorrectlyAcceptedInCaseOfASuccessfulRequestToReceiveAllLinksAndReturnedAResponseContainingTheExpectedArguments() {
        setUpServerGet("/links", getAllLinksAnswer);
        var exceptedSize = 2;
        var exceptedUrl1 = URI.create("https://github.com/TheMLord/tinkoff-java-backend-course-2023-second-semester1");
        var exceptedUrl2 = URI.create("https://github.com/TheMLord/tinkoff-java-backend-course-2023-second-semester2");

        var response = scrapperProxy.getListLinks(1L).block();
        assertThat(response.getSize()).isEqualTo(exceptedSize);
        var listLinks = response.getLinks().stream().map(LinkResponse::getUrl).toList();
        assertThat(listLinks).containsOnly(exceptedUrl1, exceptedUrl2);
    }

    @SneakyThrows
    @Test
    @DisplayName("Test that correctly accepts the error and recognizes its contents")
    void testThatCorrectlyAcceptsTheErrorAndRecognizesItsContents() {
        setUpServerError("/links", apiErrorResponseAnswer);
        var exceptedCode = "406";
        var exceptedException = "DoubleRegistrationException";
        var exceptionDescription = "Чат уже зарегистрирован";

        assertThatThrownBy(() -> scrapperProxy.getListLinks(1L).block())
            .isInstanceOf(ScrapperApiException.class);

        StepVerifier.create(scrapperProxy.getListLinks(1L))
            .expectErrorMatches(throwable ->
                throwable instanceof ScrapperApiException &&
                    ((ScrapperApiException) throwable).getApiErrorResponse().getCode().equals(exceptedCode) &&
                    ((ScrapperApiException) throwable).getApiErrorResponse().getExceptionName()
                        .equals(exceptedException) &&
                    ((ScrapperApiException) throwable).getApiErrorResponse().getDescription()
                        .equals(exceptionDescription))
            .verify();
    }

    @Test
    @DisplayName("Test the test is that there is no error when you correctly request to add a chat")
    void testTheTestIsThatThereIsNoErrorWhenYouCorrectlyRequestToAddAChat() {
        setUpServerChatPostRequest();
        var chatId = 1L;
        scrapperProxy.registerChat(chatId).block();
    }

    @Test
    @DisplayName("Test the test is that there is no error when the chat is deleted correctly")
    void testTheTestIsThatThereIsNoErrorWhenTheChatIsDeletedCorrectly() {
        setUpServerChatDeleteRequest();
        var chatId = 1L;

        scrapperProxy.deleteChat(chatId).block();
    }

    private void setUpServerChatPostRequest() {
        stubFor(
            post(urlPathMatching("/tg-chat/.*"))
                .willReturn(aResponse()
                    .withStatus(200))
        );
    }

    private void setUpServerChatDeleteRequest() {
        stubFor(
            delete(urlPathMatching("/tg-chat/.*"))
                .willReturn(aResponse()
                    .withStatus(200))
        );
    }

    private void setUpServerPost(String url, String body) {
        stubFor(
            post(urlPathMatching(url))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        body
                    )
                )
        );
    }

    private void setUpServerDelete(String url, String body) {
        stubFor(
            delete(urlPathMatching(url))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        body
                    )
                )
        );
    }

    private void setUpServerGet(String url, String body) {
        stubFor(
            get(urlPathMatching(url))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        body
                    )));
    }

    private void setUpServerError(String url, String body) {
        stubFor(
            get(urlPathMatching(url))
                .willReturn(aResponse()
                    .withStatus(406)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        body
                    )));
    }

    @DynamicPropertySource
    private static void setScrapperProxyProperty(DynamicPropertyRegistry registry) {
        registry.add("app.scrapper-base-uri", () -> "http://localhost:8090");
    }
}
