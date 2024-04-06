package edu.java.scrapper.processors;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.models.dto.GithubBranchesDTO;
import edu.java.models.pojo.GithubContent;
import edu.java.models.pojo.StackoverflowContent;
import edu.java.processors.UriProcessor;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Arrays;
import edu.java.schedulers.LinkUpdaterScheduler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static edu.java.scrapper.IntegrationEnvironment.POSTGRES;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext
@TestPropertySource(locations = "classpath:test")
public class UriProcessorsTest {
    @Autowired UriProcessor uriProcessor;
    @MockBean LinkUpdaterScheduler linkUpdaterScheduler;
    private static final String GITHUB_CONTENT_PREV =
        """
            {
            "githubRepositoryDTO":
            {
                "created_at":"2024-02-05T09:23:06Z",
                "updated_at":"2024-02-05T09:25:22Z",
                "pushed_at":"2024-02-22T10:12:19Z",
                "owner":{
                    "login":"TheMLord",
                    "id":113773994
                    }
                },
                "githubBranchesDTO":
                [
                    {"name":"main"},
                    {"name":"master"}
                ]
            }""";

    private static final String STACKOVERFLOW_PREV_CONTENT =
        """
            {"stackoverflowQuestionDTO":{"items":[{"answer_count":1,"owner":{"account_id":10650147,"display_name":"Suule"}}]},"stackoverflowAnswersDTO":{"items":[{"body":"There are several related","owner":{"reputation":375,"display_name":"tabbyfoo"}}]}}""";

    @Nested
    @DisplayName("test method processUri")
    @WireMockTest(httpPort = 8080)
    class ProcessUriMethodTests {
        @Test
        @DisplayName("Test that an unknown link is not being processed and returned a null object")
        void testThatAnUnknownLinkIsNotBeingProcessedAndReturnedANullObject() {
            var unsupportedLink = URI.create("https://habr.com/ru/companies/otus/articles/687004/");

            var actualProccessObject = uriProcessor.processUri(unsupportedLink);

            assertThat(actualProccessObject).isNull();
        }

        @Test
        @DisplayName(
            "Test that the processing of the link to the github repository is supported and returned the correct content")
        void testThatTheProcessingOfTheLinkToTheGithubRepositoryIsSupportedAndReturnedTheCorrectContent() {
            setUpServer();
            long exceptedOwnerId = 113773994L;
            var exceptedOwnerLogin = "TheMLord";
            var exceptedTimeCreatedAt = OffsetDateTime.parse("2024-02-05T09:23:06Z");
            var exceptedTimeUpdatedAt = OffsetDateTime.parse("2024-02-05T09:25:22Z");
            var exceptedTimePushedAt = OffsetDateTime.parse("2024-02-13T13:34:39Z");
            var exceptedBranches1 = "main";
            var exceptedBranches2 = "master";
            var exceptedBranches3 = "createdAccount";

            var githubRepositoryLink =
                URI.create("https://github.com/TheMLord/tinkoff-java-backend-course-2023-second-semester");

            var actualProcessObject = uriProcessor.processUri(githubRepositoryLink);

            assertThat(actualProcessObject).isNotNull();
            assertThat(actualProcessObject).isInstanceOf(GithubContent.class);

            var githubContent = (GithubContent) actualProcessObject;

            assertThat(githubContent).isNotNull();
            assertThat(githubContent.getGithubRepositoryDTO().owner().id()).isEqualTo(exceptedOwnerId);
            assertThat(githubContent.getGithubRepositoryDTO().owner().login()).isEqualTo(exceptedOwnerLogin);
            assertThat(githubContent.getGithubRepositoryDTO().createdAt()).isEqualTo(exceptedTimeCreatedAt);
            assertThat(githubContent.getGithubRepositoryDTO().updatedAt()).isEqualTo(exceptedTimeUpdatedAt);
            assertThat(githubContent.getGithubRepositoryDTO().pushedAt()).isEqualTo(exceptedTimePushedAt);
            assertThat(Arrays.stream(githubContent.getGithubBranchesDTO()).map(GithubBranchesDTO::name)
                .toList()).containsOnly(exceptedBranches1, exceptedBranches2, exceptedBranches3);
        }

        @Test
        @DisplayName(
            "Test that is supported is the processing of the link to the question in stacoverflow and returned the correct content")
        void testThatIsSupportedIsTheProcessingOfTheLinkToTheQuestionInStacoverflowAndReturnedTheCorrectContent() {
            setUpServer();
            long exceptedOwnerId = 2147L;
            var exceptedOwnerName = "Stu Thompson";
            int exceptedCountAnswer = 1;
            var exceptedSizeAnswer = 1;
            var exceptedReputationWhoAnswered = 15809;
            var exceptedNameWhoAnswered = "BigJump";
            var exceptedBodyAnswer = "ed more clearly as XML.";

            var stackoverflowQuestionLink =
                URI.create("https://stackoverflow.com/questions/82223/lravel-inertiajs-vue-3-file-upload-not-working");

            var actualProcessObject = uriProcessor.processUri(stackoverflowQuestionLink);

            assertThat(actualProcessObject).isNotNull();
            assertThat(actualProcessObject).isInstanceOf(StackoverflowContent.class);

            var stackoverflowContent = ((StackoverflowContent) actualProcessObject);
            System.out.println(stackoverflowContent.getStackoverflowQuestionDTO().items().getFirst());
            System.out.println(stackoverflowContent.getStackoverflowAnswersDTO().items().getFirst());

            assertThat(stackoverflowContent.getStackoverflowQuestionDTO().items().getFirst().owner()
                .accountId()).isEqualTo(exceptedOwnerId);
            assertThat(stackoverflowContent.getStackoverflowQuestionDTO().items().getFirst().owner()
                .displayName()).isEqualTo(exceptedOwnerName);
            assertThat(stackoverflowContent.getStackoverflowQuestionDTO().items().getFirst().answerCount()).isEqualTo(
                exceptedCountAnswer);
            assertThat(stackoverflowContent.getStackoverflowAnswersDTO().items().size()).isEqualTo(exceptedSizeAnswer);
            assertThat(stackoverflowContent.getStackoverflowAnswersDTO().items().getFirst().owner().name()).isEqualTo(
                exceptedNameWhoAnswered);
            assertThat(stackoverflowContent.getStackoverflowAnswersDTO().items().getFirst().owner()
                .reputation()).isEqualTo(exceptedReputationWhoAnswered);
            assertThat(stackoverflowContent.getStackoverflowAnswersDTO().items().getFirst().body()).isEqualTo(
                exceptedBodyAnswer);
        }
    }

    @Nested
    @DisplayName("test method compareContent")
    @WireMockTest(httpPort = 8080)
    class CompareContentTests {
        @Test
        @DisplayName("Test that the content of unsupported links is not being compared")
        void testThatTheContentOfUnsupportedLinksIsNotBeingCompared() {
            var unsupportedLink = URI.create("https://habr.com/ru/companies/otus/articles/687004/");
            var revContent = "";

            var actualLinkChanges = uriProcessor.compareContent(unsupportedLink, revContent);

            assertThat(actualLinkChanges).isEmpty();
        }

        @Test
        @DisplayName(
            "Test that the content of the link to the repository in github is compared and returned a non-empty Linkchanger with different content")
        void testThatTheContentOfTheLinkToTheRepositoryInGithubIsComparedAndReturnedANonEmptyLinkchangerWithDifferentContent() {
            setUpServer();
            var githubRepositoryLink =
                URI.create("https://github.com/TheMLord/tinkoff-java-backend-course-2023-second-semester");
            var exceptedChangesDescription = """
                Added 1 branch(es):
                createdAccount""";

            var actualLinkChanges = uriProcessor.compareContent(githubRepositoryLink, GITHUB_CONTENT_PREV);

            assertThat(actualLinkChanges).isPresent();
            var actualChanges = actualLinkChanges.get();

            assertThat(actualChanges.descriptionChanges()).isEqualTo(exceptedChangesDescription);
            assertThat(actualChanges.linkName()).isEqualTo(githubRepositoryLink);
        }

        @Test
        @DisplayName(
            "Test that the content of the link to the question in stackoverflow is compared and returned a non-empty Linkchanger with different content")
        void testThatTheContentOfTheLinkToTheQuestionInStackoverflowIsComparedAndReturnedANonEmptyLinkchangerWithDifferentContent() {
            setUpServer();
            var stackoverflowQuestionLink =
                URI.create("https://stackoverflow.com/questions/82223/lravel-inertiajs-vue-3-file-upload-not-working");
            var exceptedChangesDescription = """
                Deleted 1 answer(s):
                Author: tabbyfoo (reputations: 375)
                There are several related
                Added 1 answer(s):
                Author: BigJump (reputations: 15809)
                ed more clearly as XML.""";

            var actualLinkChanges = uriProcessor.compareContent(stackoverflowQuestionLink, STACKOVERFLOW_PREV_CONTENT);

            assertThat(actualLinkChanges).isPresent();
            var actualChanges = actualLinkChanges.get();

            assertThat(actualChanges.descriptionChanges()).isEqualTo(exceptedChangesDescription);
            assertThat(actualChanges.linkName()).isEqualTo(stackoverflowQuestionLink);
        }
    }

    @DynamicPropertySource
    static void jdbcProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.liquibase.enabled", () -> false);
        registry.add("app.database-access-type", () -> "jooq");
    }

    private void setUpServer() {
        stubFor(
            get(urlPathMatching("/repos/TheMLord/tinkoff-java-backend-course-2023-second-semester([1-9][1-9]|[1-9]*)"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
                            {
                                "owner": {
                                    "login": "TheMLord",
                                    "id": 113773994
                                },
                                "created_at": "2024-02-05T09:23:06Z",
                                "updated_at": "2024-02-05T09:25:22Z",
                                "pushed_at": "2024-02-13T13:34:39Z"
                            }"""
                    )));

        stubFor(
            get(urlPathMatching(
                "/repos/TheMLord/tinkoff-java-backend-course-2023-second-semester([1-9][1-9]|[1-9]*)/branches"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
                            [
                                 {
                                     "name": "createdAccount",
                                     "commit": {
                                         "sha": "91c6ce32c18cd16baae811a6348ea37ca75a4cdb",
                                         "url": "https://api.github.com/repos/TheMLord/Interface_for_an_ATM_with_a_banking_system/commits/91c6ce32c18cd16baae811a6348ea37ca75a4cdb"
                                     },
                                     "protected": false
                                 },
                                 {
                                     "name": "main",
                                     "commit": {
                                         "sha": "0f9906316ed03aeae035ae851b9742a630b5b070",
                                         "url": "https://api.github.com/repos/TheMLord/Interface_for_an_ATM_with_a_banking_system/commits/0f9906316ed03aeae035ae851b9742a630b5b070"
                                     },
                                     "protected": false
                                 },
                                 {
                                     "name": "master",
                                     "commit": {
                                         "sha": "af16415266c8482501b840c46411cce4a9e1f775",
                                         "url": "https://api.github.com/repos/TheMLord/Interface_for_an_ATM_with_a_banking_system/commits/af16415266c8482501b840c46411cce4a9e1f775"
                                     },
                                     "protected": false
                                 }
                             ]"""
                    )));
        stubFor(
            get(urlPathMatching("/2\\.3/questions/82223/(?!answers).*"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
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
                                        "answer_count": 1,
                                        "body": " was developed with a hand crafted hibernate mapping file."
                                    }
                                ],
                                "quota_remaining": 285
                            }"""
                    )));
        stubFor(
            get(urlPathMatching("/2\\.3/questions/82223/answers.*"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
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
                                         "body": "ed more clearly as XML."
                                     }
                                 ],
                                 "has_more": false,
                                 "quota_max": 300,
                                 "quota_remaining": 284
                             }"""
                    )));

    }
}
