package edu.java;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.models.dto.GithubBranchesDTO;
import edu.java.proxies.GithubProxy;
import edu.java.proxies.StackoverflowProxy;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Objects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static edu.java.scrapper.IntegrationEnvironment.POSTGRES;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ScrapperApplication.class)
@TestPropertySource(locations = "classpath:test")
@WireMockTest(httpPort = 8080)
class WebClientTest {
    @Autowired
    GithubProxy githubProxy;

    @Autowired
    StackoverflowProxy stackoverflowProxy;

    private static final String GITHUB_REPOSITORY_BODY =
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
    private static final String GITHUB_BRANCHES_BODY =
        """
            [
                {
                    "name": "develop",
                    "commit": {
                        "sha": "4dbe31d95af7ba0d17fba874826d11ff4694ae0d",
                        "url": "https://api.github.com/repos/TheMLord/tinkoff-java-backend-course-2023-second-semester/commits/4dbe31d95af7ba0d17fba874826d11ff4694ae0d"
                    },
                    "protected": false
                },
                {
                    "name": "main",
                    "commit": {
                        "sha": "d65e347a7168e1d9241c33b1f29b89d132c8cd29",
                        "url": "https://api.github.com/repos/TheMLord/tinkoff-java-backend-course-2023-second-semester/commits/d65e347a7168e1d9241c33b1f29b89d132c8cd29"
                    },
                    "protected": false
                },
                {
                    "name": "master",
                    "commit": {
                        "sha": "a4c23bae185c2a3459af5c3b68fdf7eb785fc6d9",
                        "url": "https://api.github.com/repos/TheMLord/tinkoff-java-backend-course-2023-second-semester/commits/a4c23bae185c2a3459af5c3b68fdf7eb785fc6d9"
                    },
                    "protected": false
                }
            ]""";
    private static final String STACKOVERFLOW_QUESTION_BODY =
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
    private static final String STACKOVERFLOW_ANSWERS_BODY = """
        {
            "items": [
                {
                    "owner": {
                        "account_id": 5339,
                        "reputation": 15809,
                        "user_id": 8542,
                        "user_type": "registered",
                        "profile_image": "https://i.stack.imgur.com/g3kyp.jpg?s=256&g=1",
                        "display_name": "BigJump",
                        "link": "https://stackoverflow.com/users/8542/bigjump"
                    },
                    "is_accepted": true,
                    "score": 7,
                    "last_activity_date": 1221652071,
                    "creation_date": 1221652071,
                    "answer_id": 82270,
                    "question_id": 82223,
                    "content_license": "CC BY-SA 2.5",
                    "body": "If it ain't broken"
                }
            ],
            "has_more": false,
            "quota_max": 300,
            "quota_remaining": 283
        }
        """;

    @Test
    @DisplayName("Test that github client parses the request correctly returned the correct data to the dto")
    void testThatGithubClientParsesTheRequestCorrectlyReturnedTheCorrectDataToTheDto() {
        long exceptedOwnerId = 113773994L;
        var exceptedOwnerLogin = "TheMLord";
        var exceptedTimeCreatedAt = OffsetDateTime.parse("2024-02-05T09:23:06Z");
        var exceptedTimeUpdatedAt = OffsetDateTime.parse("2024-02-05T09:25:22Z");
        var exceptedTimePushedAt = OffsetDateTime.parse("2024-02-13T13:34:39Z");

        setUpServer("/repos/.*", GITHUB_REPOSITORY_BODY);

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
    @DisplayName(
        "Test that the github client correctly parses the information from the request to receive repository branches and returned the expected values")
    void testThatTheGithubClientCorrectlyParsesTheInformationFromTheRequestToReceiveRepositoryBranchesAndReturnedTheExpectedValues() {
        var exceptedBranch1 = "main";
        var exceptedBranch2 = "master";
        var exceptedBranch3 = "develop";

        setUpServer("/repos/.*", GITHUB_BRANCHES_BODY);

        var response =
            githubProxy.getBranchesRequest("TheMLord", "tinkoff-java-backend-course-2023-second-semester").block();

        assertThat(response).isNotNull();
        assertThat(Arrays.stream(response).map(GithubBranchesDTO::name).toList()).containsOnly(
            exceptedBranch1,
            exceptedBranch2,
            exceptedBranch3
        );
    }

    @Test
    @DisplayName("Test that stackoverflow client parses the request correctly returned the correct data to the dto")
    void testThatStackoverflowClientParsesTheRequestCorrectlyReturnedTheCorrectDataToTheDto() {
        long exceptedOwnerId = 2147L;
        var exceptedOwnerName = "Stu Thompson";
        int exceptedCountAnswer = 8;

        setUpServer("/2\\.3/questions/.*", STACKOVERFLOW_QUESTION_BODY);

        var response = stackoverflowProxy.getQuestionRequest("82223")
            .block();
        var dto = Objects.requireNonNull(response).items().getFirst();

        assertThat(dto.owner().accountId()).isEqualTo(exceptedOwnerId);
        assertThat(dto.owner().displayName()).isEqualTo(exceptedOwnerName);
        assertThat(dto.answerCount()).isEqualTo(exceptedCountAnswer);
    }

    @Test
    @DisplayName(
        "Test that the stackoverflow client correctly parses the information from the request for answers to the question and returned the expected values")
    void testThatTheStackoverflowClientCorrectlyParsesTheInformationFromTheRequestForAnswersToTheQuestionAndReturnedTheExpectedValues() {
        var exceptedAnswerCount = 1;
        var exceptedOwnerAnswerName = "BigJump";
        var exceptedOwnerAnswerReputation = 15809;
        var exceptedOwnerAnswerBody = "If it ain't broken";

        setUpServer("/2\\.3/questions/.*", STACKOVERFLOW_ANSWERS_BODY);
        var response = stackoverflowProxy.getAnswersForQuestion("82223").block();

        assertThat(response).isNotNull();
        var dto = response.items();
        assertThat(dto.size()).isEqualTo(exceptedAnswerCount);
        assertThat(dto.getFirst().body()).isEqualTo(exceptedOwnerAnswerBody);
        assertThat(dto.getFirst().owner().name()).isEqualTo(exceptedOwnerAnswerName);
        assertThat(dto.getFirst().owner().reputation()).isEqualTo(exceptedOwnerAnswerReputation);

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

    @DynamicPropertySource
    static void jdbcProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.liquibase.enabled", () -> false);
    }
}
