package edu.java;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.proxies.GithubProxy;
import edu.java.proxies.StackoverflowProxy;
import java.time.OffsetDateTime;
import java.util.Objects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ScrapperApplication.class)
@TestPropertySource(locations = "classpath:test")
@WireMockTest(httpPort = 8080)
class WebClientTest {
    @Autowired
    GithubProxy githubProxy;

    @Autowired
    StackoverflowProxy stackoverflowProxy;

    private static final String GITHUB_ANSWER_BODY =
        """
            {
                "owner": {
                    "login": "TheMLord",
                    "id": 113773994
                },
                "created_at": "2024-02-05T09:23:06Z",
                "updated_at": "2024-02-05T09:25:22Z",
                "pushed_at": "2024-02-13T13:34:39Z"
            }""";

    private static final String STACKOVERFLOW_ANSWER_BODY =
        """
            {
                "items": [
                    {
                        "tags": [
                            "java",
                            "hibernate",
                            "annotations"
                        ],
                        "owner": {
                            "account_id": 2147,
                            "display_name": "Stu Thompson"
                        },
                        "answer_count": 8,
                        "body": "<p>I've got a webapp whose original code base was developed with a hand crafted hibernate mapping file.  Since then, I've become fairly proficient at 'coding' my hbm.xml file.  But all the cool kids are using annotations these days.</p>\\n\\n<p>So, the question is: <strong>Is it worth the effort</strong> to refactor my code to use hibernate annotations?  Will I gain anything, other than being hip and modern?  Will I lose any of the control I have in my existing hand coded mapping file?</p>\\n\\n<p>A sub-question is, <strong>how much effort will it be?</strong>  I like my databases lean and mean.  The mapping covers only a dozen domain objects, including two sets, some subclassing, and about 8 tables.</p>\\n\\n<p>Thanks, dear SOpedians, in advance for your informed opinions.</p>\\n"
                    }
                ],
                "quota_remaining": 285
            }""";

    @Test
    @DisplayName("Test that github client parses the request correctly returned the correct data to the dto")
    void testThatGithubClientParsesTheRequestCorrectlyReturnedTheCorrectDataToTheDto() {
        long exceptedOwnerId = 113773994L;
        var exceptedOwnerLogin = "TheMLord";
        var exceptedTimeCreatedAt = OffsetDateTime.parse("2024-02-05T09:23:06Z");
        var exceptedTimeUpdatedAt = OffsetDateTime.parse("2024-02-05T09:25:22Z");
        var exceptedTimePushedAt = OffsetDateTime.parse("2024-02-13T13:34:39Z");

        setUpServer("/repos/TheMLord/tinkoff-java-backend-course-2023-second-semester", GITHUB_ANSWER_BODY);

        var response =
            githubProxy.getRepositoryRequest("TheMLord", "tinkoff-java-backend-course-2023-second-semester").block();

        assertThat(response).isNotNull();
        assertThat(response.owner().id()).isEqualTo(exceptedOwnerId);
        assertThat(response.owner().login()).isEqualTo(exceptedOwnerLogin);
        assertThat(response.createdAt()).isEqualTo(exceptedTimeCreatedAt);
        assertThat(response.updatedAt()).isEqualTo(exceptedTimeUpdatedAt);
        assertThat(response.pushedAt()).isEqualTo(exceptedTimePushedAt);
    }

    @Test
    @DisplayName("Test that stackoverflow client parses the request correctly returned the correct data to the dto")
    void testThatStackoverflowClientParsesTheRequestCorrectlyReturnedTheCorrectDataToTheDto() {
        long exceptedOwnerId = 2147L;
        var exceptedOwnerName = "Stu Thompson";
        int exceptedCountAnswer = 8;

        setUpServer("/2\\.3/questions/82223/", STACKOVERFLOW_ANSWER_BODY);

        var response = stackoverflowProxy.getQuestionRequest("82223")
            .block();
        var dto = Objects.requireNonNull(response).items().getFirst();

        assertThat(dto.owner().accountId()).isEqualTo(exceptedOwnerId);
        assertThat(dto.owner().displayName()).isEqualTo(exceptedOwnerName);
        assertThat(dto.answerCount()).isEqualTo(exceptedCountAnswer);
    }

    private void setUpServer(String url, String body) {
        stubFor(
            get(urlPathMatching(url))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        body
                    )));
    }
}
