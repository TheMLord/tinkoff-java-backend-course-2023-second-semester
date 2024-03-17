package edu.java.scrapper.services.jdbc;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.domain.jooq.tables.pojos.Link;
import edu.java.repository.LinkRepository;
import edu.java.scrapper.IntegrationEnvironment;
import edu.java.services.LinkUpdateService;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
@Sql(value = "classpath:sql/insert-link-linkupdateservice.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = "classpath:sql/clearDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@WireMockTest(httpPort = 8080)
public class JdbcLinkUpdateServiceTest extends IntegrationEnvironment {
    @Autowired LinkUpdateService jdbcLinkUpdateService;
    @Autowired LinkRepository jdbcLinkRepository;

    private static final String GITHUB_ANSWER_BODY =
        """
            {
                "owner": {
                    "login": "TheMLord",
                    "id": 113773994
                },
                "created_at": "2023-10-02T21:35:03Z",
                "updated_at": "2023-10-18T13:48:21Z",
                "pushed_at": "2023-12-17T18:16:59Z"
            }""";

    private static final Link Link1 = new Link(
        1L,
        "https://github.com/TheMLord/tinkoff-java-backend-course-2023-second-semester",
        null,
        null,
        GITHUB_ANSWER_BODY,
        null
    );

    private static final Link Link2 = new Link(
        2L,
        "https://github.com/TheMLord/tinkoff-java-backend-course-2023-second-semester2",
        null,
        null,
        """
            {
                "owner": {
                    "login": "TheMLord",
                    "id": 113773994
                },
                "created_at": "2023-10-02T21:35:03Z",
                "updated_at": "2023-10-18T13:48:21Z",
                "pushed_at": "2023-10-17T18:16:59Z"
            }""",
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
        var exceptedLinkUpdateDescription = "Есть изменения";
        var exceptedLinkUpdateChats = List.of(1L, 2L);

        var actualLinkUpdateOptional = jdbcLinkUpdateService.prepareLinkUpdate(Link2);

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
        var actualLinkUpdateOptional = jdbcLinkUpdateService.prepareLinkUpdate(Link1);

        assertThat(actualLinkUpdateOptional).isEmpty();
    }

    @Test
    @DisplayName(
        "Test that the service update of the last change time for the entity is working correctly and returned the correct time for the entity without changes")
    @Transactional
    @Rollback
    void testThatTheServiceUpdateOfTheLastChangeTimeForTheEntityIsWorkingCorrectlyAndReturnedTheCorrectTimeForTheEntityWithoutChanges() {
        setUpServer();
        var timeLastModifyingBefore = jdbcLinkRepository.findById(1L).get().getLastModifying();
        var actualLinkUpdateOptional = jdbcLinkUpdateService.prepareLinkUpdate(Link1);

        assertThat(actualLinkUpdateOptional).isEmpty();

        var timeLastModifyingAfter = jdbcLinkRepository.findById(1L).get().getLastModifying();
        assertThat(timeLastModifyingAfter).isNotEqualTo(timeLastModifyingBefore);
    }

    @Test
    @DisplayName(
        "Test that the service update of the last change time for the entity is working correctly and returned the correct time for the entity with changes")
    @Transactional
    @Rollback
    void testThatTheServiceUpdateOfTheLastChangeTimeForTheEntityIsWorkingCorrectlyAndReturnedTheCorrectTimeForTheEntityWithChanges() {
        setUpServer();
        var timeLastModifyingBefore = jdbcLinkRepository.findById(2L).get().getLastModifying();
        var actualLinkUpdateOptional = jdbcLinkUpdateService.prepareLinkUpdate(Link2);

        assertThat(actualLinkUpdateOptional).isPresent();

        var timeLastModifyingAfter = jdbcLinkRepository.findById(2L).get().getLastModifying();
        assertThat(timeLastModifyingAfter).isNotEqualTo(timeLastModifyingBefore);
    }

    @Test
    @DisplayName(
        "Test that the service update of the content for the entity is working correctly and returned the correct content for the entity without changes")
    @Transactional
    @Rollback
    void testThatTheServiceUpdateOfTheContentForTheEntityIsWorkingCorrectlyAndReturnedTheCorrectContentForTheEntityWithoutChanges() {
        setUpServer();
        var contentBefore = jdbcLinkRepository.findById(1L).get().getContent();
        var actualLinkUpdateOptional = jdbcLinkUpdateService.prepareLinkUpdate(Link1);

        assertThat(actualLinkUpdateOptional).isEmpty();

        var contentAfter = jdbcLinkRepository.findById(1L).get().getContent();
        assertThat(contentAfter).isEqualTo(contentBefore);
    }

    @Test
    @DisplayName(
        "Test that the service update of the content for the entity is working correctly and returned the correct content for the entity with changes")
    @Transactional
    @Rollback
    void testThatTheServiceUpdateOfTheContentForTheEntityIsWorkingCorrectlyAndReturnedTheCorrectContentForTheEntityWithChanges() {
        setUpServer();
        var contentBefore = jdbcLinkRepository.findById(2L).get().getContent();
        var actualLinkUpdateOptional = jdbcLinkUpdateService.prepareLinkUpdate(Link2);

        assertThat(actualLinkUpdateOptional).isPresent();

        var contentAfter = jdbcLinkRepository.findById(2L).get().getContent();
        assertThat(contentAfter).isNotEqualTo(contentBefore);

    }

    private void setUpServer() {
        stubFor(
            get(urlPathMatching("/repos/.*"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        GITHUB_ANSWER_BODY
                    )));
    }

    @DynamicPropertySource
    static void jdbcProperties(DynamicPropertyRegistry registry) {
        registry.add("app.data-access-technology", () -> "JDBC");
    }

}
