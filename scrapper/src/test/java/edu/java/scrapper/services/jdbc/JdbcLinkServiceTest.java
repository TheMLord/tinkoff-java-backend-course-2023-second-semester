package edu.java.scrapper.services.jdbc;

import edu.java.repository.LinkRepository;
import edu.java.scrapper.IntegrationTest;
import edu.java.services.LinkService;
import java.net.URI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(value = "classpath:sql/jdbcservice-insert-test.sql",
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = "classpath:sql/clearDB.sql",
     executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@TestPropertySource(locations = "classpath:test")
public class JdbcLinkServiceTest extends IntegrationTest {
    @Autowired LinkService jdbcLinkService;
    @Autowired LinkRepository jdbcLinkRepository;

    @Test
    @DisplayName(
        "Test that the add link between chat and link method works correctly and returns the correct LinkResponse")
    @Transactional
    @Rollback
    void testThatTheAddLinkBetweenChatAndLinkMethodWorksCorrectlyAndReturnsTheCorrectLinkResponse() {
        var exceptedLinkResponseURI =
            URI.create("https://github.com/TheMLord/java-backend-course-2023-tinkoff2");
        var exceptedLinkResponseId = jdbcLinkRepository
            .findLinkByName(exceptedLinkResponseURI).get().getId();

        var actualLinkResponse = jdbcLinkService.addLink(2L, exceptedLinkResponseURI);

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
        var exceptedLinkResponseId = jdbcLinkRepository
            .findLinkByName(exceptedLinkResponseURI).get().getId();

        var actualLinkResponse = jdbcLinkService.removeLink(1L, exceptedLinkResponseURI);

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

        var actualListLinkResponse = jdbcLinkService.getListLinks(1L);
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
}
