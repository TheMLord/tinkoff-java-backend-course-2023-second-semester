package edu.java;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.proxies.ClientProxy;
import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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
import static org.assertj.core.api.Assertions.fail;

@SpringBootTest(classes = ScrapperApplication.class)
@TestPropertySource(locations = "classpath:test")
@WireMockTest(httpPort = 8080)
class WebClientTest {
    @Autowired
    ClientProxy clientProxy;

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
                            "reputation": 38620,
                            "user_id": 2961,
                            "user_type": "registered",
                            "accept_rate": 79,
                            "profile_image": "https://www.gravatar.com/avatar/d5f30c2d40341d55ba235d674d24972e?s=256&d=identicon&r=PG",
                            "display_name": "Stu Thompson",
                            "link": "https://stackoverflow.com/users/2961/stu-thompson"
                        },
                        "is_answered": true,
                        "view_count": 681,
                        "accepted_answer_id": 82270,
                        "answer_count": 8,
                        "score": 2,
                        "last_activity_date": 1450206384,
                        "creation_date": 1221651428,
                        "last_edit_date": 1221651959,
                        "question_id": 82223,
                        "content_license": "CC BY-SA 2.5",
                        "link": "https://stackoverflow.com/questions/82223/is-it-worth-the-effort-to-move-from-a-hand-crafted-hibernate-mapping-file-to-ann",
                        "title": "Is it worth the effort to move from a hand crafted hibernate mapping file to annotaions?",
                        "body": "<p>I've got a webapp whose original code base was developed with a hand crafted hibernate mapping file.  Since then, I've become fairly proficient at 'coding' my hbm.xml file.  But all the cool kids are using annotations these days.</p>\\n\\n<p>So, the question is: <strong>Is it worth the effort</strong> to refactor my code to use hibernate annotations?  Will I gain anything, other than being hip and modern?  Will I lose any of the control I have in my existing hand coded mapping file?</p>\\n\\n<p>A sub-question is, <strong>how much effort will it be?</strong>  I like my databases lean and mean.  The mapping covers only a dozen domain objects, including two sets, some subclassing, and about 8 tables.</p>\\n\\n<p>Thanks, dear SOpedians, in advance for your informed opinions.</p>\\n"
                    }
                ],
                "has_more": false,
                "quota_max": 300,
                "quota_remaining": 285
            }""";

    @Test
    @DisplayName("Test that github client parses the request correctly returned the correct data to the dto")
    void testThatGithubClientParsesTheRequestCorrectlyReturnedTheCorrectDataToTheDto()
        throws ExecutionException, InterruptedException {
        var completableFuture = new CompletableFuture<>();

        long exceptedOwnerId = 113773994L;
        var exceptedOwnerLogin = "TheMLord";
        var exceptedTimeCreatedAt = OffsetDateTime.parse("2024-02-05T09:23:06Z");
        var exceptedTimeUpdatedAt = OffsetDateTime.parse("2024-02-05T09:25:22Z");
        var exceptedTimePushedAt = OffsetDateTime.parse("2024-02-13T13:34:39Z");
        setUpServer("/repos/TheMLord/tinkoff-java-backend-course-2023-second-semester", GITHUB_ANSWER_BODY);

        clientProxy.createGithubRequest("TheMLord", "tinkoff-java-backend-course-2023-second-semester")
            .subscribe(response -> {
                    assertThat(response.owner().id()).isEqualTo(exceptedOwnerId);
                    assertThat(response.owner().login()).isEqualTo(exceptedOwnerLogin);
                    assertThat(response.createdAt()).isEqualTo(exceptedTimeCreatedAt);
                    assertThat(response.updatedAt()).isEqualTo(exceptedTimeUpdatedAt);
                    assertThat(response.pushedAt()).isEqualTo(exceptedTimePushedAt);

                    completableFuture.complete(null);
                }, error -> {
                    completableFuture.complete(null);
                    fail("Ошибка запроса");
                }

            );

        completableFuture.get();
    }

    @Test
    @DisplayName("Test that stackoverflow client parses the request correctly returned the correct data to the dto")
    void testThatStackoverflowClientParsesTheRequestCorrectlyReturnedTheCorrectDataToTheDto()
        throws ExecutionException, InterruptedException {
        var completableFuture = new CompletableFuture<>();

        long exceptedOwnerId = 2147L;
        var exceptedOwnerName = "Stu Thompson";
        int exceptedCountAnswer = 8;

        setUpServer("/2\\.3/questions/82223/", STACKOVERFLOW_ANSWER_BODY);
        clientProxy.createStackoverflowRequest("82223")
            .subscribe(
                response -> {
                    var dto = response.items().getFirst();

                    assertThat(dto.owner().accountId()).isEqualTo(exceptedOwnerId);
                    assertThat(dto.owner().displayName()).isEqualTo(exceptedOwnerName);
                    assertThat(dto.answerCount()).isEqualTo(exceptedCountAnswer);

                    completableFuture.complete(null);
                },
                error -> {
                    completableFuture.complete(null);
                    fail("Ошибка запроса");
                }
            );

        completableFuture.get();
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
