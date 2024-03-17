package edu.java.scrapper.processors;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.models.dto.GithubRepositoryDTO;
import edu.java.models.dto.StackoverflowDTO;
import edu.java.processors.UriProcessor;
import java.net.URI;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

@SpringBootTest
@TestPropertySource(locations = "classpath:test")
public class UriProcessorsTest {
    @Autowired UriProcessor uriProcessor;

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
    private static final String GITHUB_CONTENT_PREV =
        """
            {
                "owner": {
                    "login": "TheMLord",
                    "id": 113773994
                },
                "created_at": "2024-02-05T09:23:06Z",
                "updated_at": "2024-02-05T09:25:22Z",
                "pushed_at": "2024-01-13T13:34:39Z"
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

    private static final String STACKOVERFLOW_PREV_CONTENT =
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
                        "answer_count": 6,
                        "body": "<p>I've got a webapp whose original code base was developed with a hand crafted hibernate mapping file.  Since then, I've become fairly proficient at 'coding' my hbm.xml file.  But all the cool kids are using annotations these days.</p>\\n\\n<p>So, the question is: <strong>Is it worth the effort</strong> to refactor my code to use hibernate annotations?  Will I gain anything, other than being hip and modern?  Will I lose any of the control I have in my existing hand coded mapping file?</p>\\n\\n<p>A sub-question is, <strong>how much effort will it be?</strong>  I like my databases lean and mean.  The mapping covers only a dozen domain objects, including two sets, some subclassing, and about 8 tables.</p>\\n\\n<p>Thanks, dear SOpedians, in advance for your informed opinions.</p>\\n"
                    }
                ],
                "quota_remaining": 285
            }""";

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
            setUpServer("/repos/.*", GITHUB_ANSWER_BODY);
            long exceptedOwnerId = 113773994L;
            var exceptedOwnerLogin = "TheMLord";
            var exceptedTimeCreatedAt = OffsetDateTime.parse("2024-02-05T09:23:06Z");
            var exceptedTimeUpdatedAt = OffsetDateTime.parse("2024-02-05T09:25:22Z");
            var exceptedTimePushedAt = OffsetDateTime.parse("2024-02-13T13:34:39Z");
            var githubRepositoryLink = URI.create("https://github.com/TheMLord/java-backend-course-2023-tinkoff");

            var actualProcessObject = uriProcessor.processUri(githubRepositoryLink);

            assertThat(actualProcessObject).isNotNull();
            assertThat(actualProcessObject).isInstanceOf(GithubRepositoryDTO.class);

            var githubDTO = (GithubRepositoryDTO) actualProcessObject;

            assertThat(githubDTO).isNotNull();
            assertThat(githubDTO.owner().id()).isEqualTo(exceptedOwnerId);
            assertThat(githubDTO.owner().login()).isEqualTo(exceptedOwnerLogin);
            assertThat(githubDTO.createdAt()).isEqualTo(exceptedTimeCreatedAt);
            assertThat(githubDTO.updatedAt()).isEqualTo(exceptedTimeUpdatedAt);
            assertThat(githubDTO.pushedAt()).isEqualTo(exceptedTimePushedAt);
        }

        @Test
        @DisplayName(
            "Test that is supported is the processing of the link to the question in stacoverflow and returned the correct content")
        void testThatIsSupportedIsTheProcessingOfTheLinkToTheQuestionInStacoverflowAndReturnedTheCorrectContent() {
            setUpServer("/2\\.3/questions/.*", STACKOVERFLOW_ANSWER_BODY);
            long exceptedOwnerId = 2147L;
            var exceptedOwnerName = "Stu Thompson";
            int exceptedCountAnswer = 8;
            var stackoverflowQuestionLink =
                URI.create("https://stackoverflow.com/questions/78171066/lravel-inertiajs-vue-3-file-upload-not-working");

            var actualProcessObject = uriProcessor.processUri(stackoverflowQuestionLink);

            assertThat(actualProcessObject).isNotNull();
            assertThat(actualProcessObject).isInstanceOf(StackoverflowDTO.class);

            var stackoverflowDTO = ((StackoverflowDTO) actualProcessObject).items().getFirst();

            assertThat(stackoverflowDTO.owner().accountId()).isEqualTo(exceptedOwnerId);
            assertThat(stackoverflowDTO.owner().displayName()).isEqualTo(exceptedOwnerName);
            assertThat(stackoverflowDTO.answerCount()).isEqualTo(exceptedCountAnswer);
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
            "Test that the content of the link to the repository in github is compared and returned an empty Linkchanger with the same content")
        void testThatTheContentOfTheLinkToTheRepositoryInGithubIsComparedAndReturnedAnEmptyLinkchangerWithTheSameContent() {
            setUpServer("/repos/.*", GITHUB_ANSWER_BODY);
            var githubRepositoryLink = URI.create("https://github.com/TheMLord/java-backend-course-2023-tinkoff");

            var actualLinkChanges = uriProcessor.compareContent(githubRepositoryLink, GITHUB_ANSWER_BODY);

            assertThat(actualLinkChanges).isEmpty();
        }

        @Test
        @DisplayName(
            "Test that the content of the link to the question in stackoverflow is compared and returned an empty Linkchanger with the same content")
        void testThatTheContentOfTheLinkToTheQuestionInStackoverflowIsComparedAndReturnedAnEmptyLinkchangerWithTheSameContent() {
            setUpServer("/2\\.3/questions/.*", STACKOVERFLOW_ANSWER_BODY);
            var stackoverflowQuestionLink =
                URI.create("https://stackoverflow.com/questions/78171066/lravel-inertiajs-vue-3-file-upload-not-working");

            var actualLinkChanges = uriProcessor.compareContent(stackoverflowQuestionLink, STACKOVERFLOW_ANSWER_BODY);

            assertThat(actualLinkChanges).isEmpty();
        }

        @Test
        @DisplayName(
            "Test that the content of the link to the repository in github is compared and returned a non-empty Linkchanger with different content")
        void testThatTheContentOfTheLinkToTheRepositoryInGithubIsComparedAndReturnedANonEmptyLinkchangerWithDifferentContent() {
            setUpServer("/repos/.*", GITHUB_ANSWER_BODY);
            var githubRepositoryLink = URI.create("https://github.com/TheMLord/java-backend-course-2023-tinkoff");
            var exceptedChangesDescription = "Есть изменения";

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
            setUpServer("/2\\.3/questions/.*", STACKOVERFLOW_ANSWER_BODY);
            var stackoverflowQuestionLink =
                URI.create("https://stackoverflow.com/questions/78171066/lravel-inertiajs-vue-3-file-upload-not-working");
            var exceptedChangesDescription = "Есть изменения";

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
    }

    private void setUpServer(String uri, String body) {
        stubFor(
            get(urlPathMatching(uri))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        body
                    )));
    }
}
