package edu.java.scrapper.services.jdbc;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.schedulers.LinkUpdaterScheduler;
import edu.java.scrapper.IntegrationEnvironment;
import edu.java.servicies.LinkUpdateCheckService;
import java.net.URI;
import java.util.List;
import org.apache.kafka.clients.admin.AdminClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaAdmin;
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
@Sql(value = "classpath:sql/insert-for-link-update-service.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = "classpath:sql/clearDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@DirtiesContext
@WireMockTest(httpPort = 8080)
public class JdbcLinkUpdateServiceTest extends IntegrationEnvironment {
    @MockBean AdminClient adminClient;
    @MockBean KafkaAdmin kafkaAdmin;

    @Autowired LinkUpdateCheckService linkUpdateService;
    @MockBean LinkUpdaterScheduler linkUpdaterScheduler;

    private static final String GITHUB_BRANCHES =
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
             ]""";
    private static final String GITHUB_BRANCHES_WITH_CHANGES;

    static {
        GITHUB_BRANCHES_WITH_CHANGES = """
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
                 }
             ]""";
    }

    @Test
    @DisplayName(
        "Test that receiving updates works correctly and a non-empty Link Update is returned if there are updates")
    @Transactional
    @Rollback
    void testThatReceivingUpdatesWorksCorrectlyAndANonEmptyLinkUpdateIsReturnedIfThereAreUpdates() {
        setUpServer(GITHUB_BRANCHES_WITH_CHANGES);
        var exceptedLinkUpdateId = 1L;
        var exceptedLinkUpdateURI =
            URI.create("https://github.com/TheMLord/tinkoff-java-backend-course-2023-second-semester");
        var exceptedLinkUpdateDescription = """
            Deleted 1 branch(es):
            master
            """;
        var exceptedLinkUpdateChats = List.of(1L, 3L);

        var actualLinkUpdate = linkUpdateService.prepareLinkUpdate().blockFirst();

        assertThat(actualLinkUpdate).isNotNull();

        assertThat(actualLinkUpdate.getId()).isEqualTo(exceptedLinkUpdateId);
        assertThat(actualLinkUpdate.getUrl()).isEqualTo(exceptedLinkUpdateURI);
        assertThat(actualLinkUpdate.getDescription()).isEqualTo(exceptedLinkUpdateDescription);
        assertThat(actualLinkUpdate.getTgChatIds()).containsAll(exceptedLinkUpdateChats);
    }

    @Test
    @DisplayName(
        "Test that receiving updates works correctly and returned an empty Link Update in the absence of updates")
    @Transactional
    @Rollback
    void testThatReceivingUpdatesWorksCorrectlyAndReturnedAnEmptyLinkUpdateInTheAbsenceOfUpdates() {
        setUpServer(GITHUB_BRANCHES);
        var actualLinkUpdate = linkUpdateService.prepareLinkUpdate().blockFirst();

        assertThat(actualLinkUpdate).isNull();
    }

    private void setUpServer(String body) {
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
                        body
                    )));
    }

    @DynamicPropertySource
    static void jdbcProperties(DynamicPropertyRegistry registry) {
        registry.add("app.database-access-type", () -> "jdbc");
    }

}
