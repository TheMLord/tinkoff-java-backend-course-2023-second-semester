package edu.java.scrapper.services.jooq;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.domain.pojos.Links;
import edu.java.repository.LinkRepository;
import edu.java.scrapper.IntegrationEnvironment;
import edu.java.services.LinkUpdateService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext
@Sql(value = "classpath:sql/insert-link-linkupdateservice.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = "classpath:sql/clearDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@WireMockTest(httpPort = 8080)
public class JooqLinkUpdateServiceTest extends IntegrationEnvironment {
    @Autowired LinkUpdateService jdbcLinkUpdateService;
    @Autowired LinkRepository jdbcLinkRepository;

    private static final String GITHUB_CONTENT =
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
                    {"name":"develop"},
                    {"name":"hw2"},
                    {"name":"hw3"},
                    {"name":"hw4"},
                    {"name":"hw5_bonus"},
                    {"name":"hw5"},
                    {"name":"main"},
                    {"name":"master"}
                ]
            }""";

    private static final Links Link1 = new Links(
        1L,
        "https://github.com/TheMLord/tinkoff-java-backend-course-2023-second-semester",
        OffsetDateTime.now(),
        null,
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
                    {"name":"createdAccount"},
                    {"name":"main"},
                    {"name":"master"}
                ]
            }""",
        null
    );

    private static final Links Link2 = new Links(
        2L,
        "https://github.com/TheMLord/tinkoff-java-backend-course-2023-second-semester2",
        OffsetDateTime.now(),
        null,
        GITHUB_CONTENT,
        null
    );

    @Test
    @DisplayName(
        "Test that receiving updates works correctly and a non-empty Link Update is returned if there are updates")
    @Transactional
    @Rollback
    void testThatReceivingUpdatesWorksCorrectlyAndANonEmptyLinkUpdateIsReturnedIfThereAreUpdates() {
        setUpServer();
        var exceptedLinkUpdateId = 2L;
        var exceptedLinkUpdateURI =
            URI.create("https://github.com/TheMLord/tinkoff-java-backend-course-2023-second-semester2");
        var exceptedLinkUpdateDescription = """
            Deleted 6 branch(es):
            develop
            hw2
            hw3
            hw4
            hw5_bonus
            hw5
            Added 1 branch(es):
            createdAccount""";
        var exceptedLinkUpdateChats = List.of(1L, 2L);

        var actualLinkUpdateOptional = jdbcLinkUpdateService.prepareLinkUpdate(Link2).block();

        assertThat(actualLinkUpdateOptional).isPresent();
        var linkUpdate = actualLinkUpdateOptional.get();

        assertThat(linkUpdate.getId()).isEqualTo(exceptedLinkUpdateId);
        assertThat(linkUpdate.getUrl()).isEqualTo(exceptedLinkUpdateURI);
        assertThat(linkUpdate.getDescription()).isEqualTo(exceptedLinkUpdateDescription);
        assertThat(linkUpdate.getTgChatIds()).containsAll(exceptedLinkUpdateChats);
    }

    @Test
    @DisplayName(
        "Test that receiving updates works correctly and returned an empty Link Update in the absence of updates")
    @Transactional
    @Rollback
    void testThatReceivingUpdatesWorksCorrectlyAndReturnedAnEmptyLinkUpdateInTheAbsenceOfUpdates() {
        setUpServer();
        var actualLinkUpdateOptional = jdbcLinkUpdateService.prepareLinkUpdate(Link1).block();

        assertThat(actualLinkUpdateOptional).isEmpty();
    }

    @Test
    @DisplayName(
        "Test that the service update of the last change time for the entity is working correctly and returned the correct time for the entity without changes")
    @Transactional
    @Rollback
    void testThatTheServiceUpdateOfTheLastChangeTimeForTheEntityIsWorkingCorrectlyAndReturnedTheCorrectTimeForTheEntityWithoutChanges() {
        setUpServer();
        var timeLastModifyingBefore = jdbcLinkRepository.findById(1L).block().get().getLastModifying();
        var actualLinkUpdateOptional = jdbcLinkUpdateService.prepareLinkUpdate(Link1).block();

        assertThat(actualLinkUpdateOptional).isEmpty();

        var timeLastModifyingAfter = jdbcLinkRepository.findById(1L).block().get().getLastModifying();
        assertThat(timeLastModifyingAfter).isNotEqualTo(timeLastModifyingBefore);
    }

    @Test
    @DisplayName(
        "Test that the service update of the last change time for the entity is working correctly and returned the correct time for the entity with changes")
    @Transactional
    @Rollback
    void testThatTheServiceUpdateOfTheLastChangeTimeForTheEntityIsWorkingCorrectlyAndReturnedTheCorrectTimeForTheEntityWithChanges() {
        setUpServer();
        var timeLastModifyingBefore = jdbcLinkRepository.findById(2L).block().get().getLastModifying();
        var actualLinkUpdateOptional = jdbcLinkUpdateService.prepareLinkUpdate(Link2).block();

        assertThat(actualLinkUpdateOptional).isPresent();

        var timeLastModifyingAfter = jdbcLinkRepository.findById(2L).block().get().getLastModifying();
        assertThat(timeLastModifyingAfter).isNotEqualTo(timeLastModifyingBefore);
    }

    @Test
    @DisplayName(
        "Test that the service update of the content for the entity is working correctly and returned the correct content for the entity without changes")
    @Transactional
    @Rollback
    void testThatTheServiceUpdateOfTheContentForTheEntityIsWorkingCorrectlyAndReturnedTheCorrectContentForTheEntityWithoutChanges() {
        setUpServer();
        var contentBefore = jdbcLinkRepository.findById(1L).block().get().getContent();
        var actualLinkUpdateOptional = jdbcLinkUpdateService.prepareLinkUpdate(Link1).block();

        assertThat(actualLinkUpdateOptional).isEmpty();

        var contentAfter = jdbcLinkRepository.findById(1L).block().get().getContent();
        assertThat(contentAfter).isEqualTo(contentBefore);
    }

    @Test
    @DisplayName(
        "Test that the service update of the content for the entity is working correctly and returned the correct content for the entity with changes")
    @Transactional
    @Rollback
    void testThatTheServiceUpdateOfTheContentForTheEntityIsWorkingCorrectlyAndReturnedTheCorrectContentForTheEntityWithChanges() {
        setUpServer();
        var contentBefore = jdbcLinkRepository.findById(2L).block().get().getContent();
        var actualLinkUpdateOptional = jdbcLinkUpdateService.prepareLinkUpdate(Link2).block();

        assertThat(actualLinkUpdateOptional).isPresent();

        var contentAfter = jdbcLinkRepository.findById(2L).block().get().getContent();
        assertThat(contentAfter).isNotEqualTo(contentBefore);

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
    }

    @DynamicPropertySource
    static void jdbcProperties(DynamicPropertyRegistry registry) {
        registry.add("app.database-access-type", () -> "jooq");
    }

}
