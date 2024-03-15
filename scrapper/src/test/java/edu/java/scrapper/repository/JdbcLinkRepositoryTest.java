package edu.java.scrapper.repository;

import edu.java.repository.LinkRepository;
import edu.java.scrapper.IntegrationTest;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
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
@Sql(value = "classpath:sql/insert-test-jdbc-linkrepository.sql",
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@TestPropertySource(locations = "classpath:test")
public class JdbcLinkRepositoryTest extends IntegrationTest {
    @Autowired LinkRepository jdbcLinkRepository;

    @Test
    @DisplayName(
        "Test that the method of searching for all links works correctly returned the correct number of links")
    @Transactional
    @Rollback
    void testThatTheMethodOfSearchingForAllLinksWorksCorrectlyReturnedTheCorrectNumberOfLinks() {
        var exceptedCountLinks = 3;

        var actualAllLinks = jdbcLinkRepository.findAll();

        assertThat(actualAllLinks.size()).isEqualTo(exceptedCountLinks);

        var actualListLinkName = actualAllLinks.stream().map(link -> link.getLinkName().toString()).toList();
        assertThat(actualListLinkName).containsOnly(
            "https://github.com/TheMLord/java-backend-course-2023-tinkoff1",
            "https://github.com/TheMLord/java-backend-course-2023-tinkoff2",
            "https://github.com/TheMLord/java-backend-course-2023-tinkoff3"
        );
    }

    @Test
    @DisplayName(
        "Test that the method works correctly with searching for all links with a filter and returned the correct links")
    @Transactional
    @Rollback
    void testThatTheMethodWorksCorrectlyWithSearchingForAllLinksWithAFilterAndReturnedTheCorrectLinks() {
        var timePredicate = OffsetDateTime.parse(
            "2024-03-15 13:49:14.240739 +00:00",
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS XXX")
        );
        var exceptedContLink = 2;

        var actualLinks = jdbcLinkRepository.findAllByTime(timePredicate);

        assertThat(actualLinks.size()).isEqualTo(exceptedContLink);
        var actualListLinkName = actualLinks.stream().map(link -> link.getLinkName().toString()).toList();
        assertThat(actualListLinkName).containsOnly(
            "https://github.com/TheMLord/java-backend-course-2023-tinkoff2",
            "https://github.com/TheMLord/java-backend-course-2023-tinkoff3"
        );
    }

    @Test
    @DisplayName("Test that works correctly is updating the last link change returned the correct time")
    @Transactional
    @Rollback
    void testThatWorksCorrectlyIsUpdatingTheLastLinkChangeReturnedTheCorrectTime() {
        var exceptedLastModifyingTime = OffsetDateTime.parse(
            "2024-03-15 17:49:14 +00:00",
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss XXX")
        );
        var idLink = jdbcLinkRepository.findLinkByName(
            URI.create("https://github.com/TheMLord/java-backend-course-2023-tinkoff1")
        ).get().getId();

        assertThat(jdbcLinkRepository.findById(idLink)).isPresent();
        jdbcLinkRepository.updateLastModifying(idLink, exceptedLastModifyingTime);
        var actualLinkLAtModifying = jdbcLinkRepository.findById(idLink).get().getLastModifying();
        assertThat(actualLinkLAtModifying).isEqualTo(exceptedLastModifyingTime);
    }

    @Test
    @DisplayName("Test that works correctly is updating the link content returned the correct content")
    @Transactional
    @Rollback
    void testThatWorksCorrectlyIsUpdatingTheLinkContentReturnedTheCorrectContent() {
        var exceptedContent = """
            {
                "example": "json"
            }
            """;
        var idLink = jdbcLinkRepository.findLinkByName(
            URI.create("https://github.com/TheMLord/java-backend-course-2023-tinkoff1")
        ).get().getId();

        assertThat(jdbcLinkRepository.findById(idLink)).isPresent();
        jdbcLinkRepository.updateContent(idLink, exceptedContent);
        var actualContent = jdbcLinkRepository.findById(idLink).get().getContent();

        assertThat(exceptedContent).isEqualTo(actualContent);
    }
}
