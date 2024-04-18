package edu.java.scrapper.services.jooq;

import edu.java.schedulers.LinkUpdaterScheduler;
import edu.java.scrapper.IntegrationEnvironment;
import edu.java.servicies.LinkService;
import java.net.URI;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(value = "classpath:sql/service-insert-test.sql",
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = "classpath:sql/clearDB.sql",
     executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@DirtiesContext
@TestPropertySource(locations = "classpath:test")
public class JooqLinkServiceTest extends IntegrationEnvironment {
    @MockBean AdminClient adminClient;
    @MockBean KafkaAdmin kafkaAdmin;

    @Autowired LinkService linkService;
    @MockBean LinkUpdaterScheduler linkUpdaterScheduler;

    @Test
    @DisplayName(
        "Test that the add link between chat and link method works correctly and returns the correct LinkResponse")
    @Transactional
    @Rollback
    void testThatTheAddLinkBetweenChatAndLinkMethodWorksCorrectlyAndReturnsTheCorrectLinkResponse() {
        var exceptedLinkResponseURI =
            URI.create("https://github.com/TheMLord/java-backend-course-2023-tinkoff2");

        var exceptedLinkResponseId = 2L;

        var actualLinkResponse = linkService.addLink(2L, exceptedLinkResponseURI).block();

        assertThat(actualLinkResponse.getId()).isEqualTo(exceptedLinkResponseId);
        assertThat(actualLinkResponse.getUrl()).isEqualTo(exceptedLinkResponseURI);
    }

    @Test
    @DisplayName(
        "Test that the method of removing the link between the chat and the link works correctly and returns the correct LinkResponse")
    @Transactional
    @Rollback
    void testThatTheMethodOfRemovingTheLinkBetweenTheChatAndTheLinkWorksCorrectlyAndReturnsTheCorrectLinkResponse() {
        var exceptedLinkResponseURI =
            URI.create("https://github.com/TheMLord/java-backend-course-2023-tinkoff2");
        var exceptedLinkResponseId = 2L;

        var actualLinkResponse = linkService.removeLink(1L, exceptedLinkResponseURI).block();

        assertThat(actualLinkResponse.getId()).isEqualTo(exceptedLinkResponseId);
        assertThat(actualLinkResponse.getUrl()).isEqualTo(exceptedLinkResponseURI);
    }

    @Test
    @DisplayName(
        "Test that the method of preparing the message about the links monitored by the chat is being performed correctly and returned the correct answer")
    @Transactional
    @Rollback
    void testThatTheMethodOfPreparingTheMessageAboutTheLinksMonitoredByTheChatIsBeingPerformedCorrectlyAndReturnedTheCorrectAnswer() {
        var exceptedSize = 3;

        var actualListLinkResponse = linkService.getListLinks(1L).block();
        var trackedLinks =
            actualListLinkResponse.getLinks().stream()
                .map(linkResponse -> linkResponse.getUrl().toString())
                .toList();

        assertThat(actualListLinkResponse.getSize()).isEqualTo(exceptedSize);
        assertThat(trackedLinks).containsOnly(
            "https://github.com/TheMLord/java-backend-course-2023-tinkoff1",
            "https://github.com/TheMLord/java-backend-course-2023-tinkoff2",
            "https://github.com/TheMLord/java-backend-course-2023-tinkoff3"
        );
    }

    @DynamicPropertySource
    static void jdbcProperties(DynamicPropertyRegistry registry) {
        registry.add("app.database-access-type", () -> "jooq");
    }
}
