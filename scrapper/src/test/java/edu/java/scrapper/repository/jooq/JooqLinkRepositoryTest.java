package edu.java.scrapper.repository.jooq;

import edu.java.domain.pojos.Links;
import edu.java.repository.LinkRepository;
import edu.java.scrapper.IntegrationEnvironment;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext
@Sql(value = "classpath:sql/insert-test-jdbc-linkrepository.sql",
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = "classpath:sql/clearDB.sql",
     executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@TestPropertySource(locations = "classpath:test")
public class JooqLinkRepositoryTest extends IntegrationEnvironment {
    @Autowired LinkRepository jdbcLinkRepository;

    @Test
    @DisplayName(
        "Test that the method of searching for all links works correctly returned the correct number of links")
    @Transactional
    @Rollback
    void testThatTheMethodOfSearchingForAllLinksWorksCorrectlyReturnedTheCorrectNumberOfLinks() {
        var exceptedCountLinks = 3;

        var actualAllLinks = jdbcLinkRepository.findAll().collectList().block();

        assertThat(actualAllLinks.size()).isEqualTo(exceptedCountLinks);

        var actualListLinkName = actualAllLinks.stream().map(Links::getLinkUri).toList();
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

        var actualLinks = jdbcLinkRepository.findAllByTime(timePredicate).collectList().block();

        assertThat(actualLinks.size()).isEqualTo(exceptedContLink);

        var actualListLinkName = actualLinks.stream().map(Links::getLinkUri).toList();

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
        ).block().getId();

        assertThat(jdbcLinkRepository.findById(idLink).block()).isNotNull();
        jdbcLinkRepository.updateLastModifying(idLink, exceptedLastModifyingTime).block();
        var actualLinkLAtModifying = jdbcLinkRepository.findById(idLink).block().getLastModifying();
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
        ).block().getId();

        assertThat(jdbcLinkRepository.findById(idLink).block()).isNotNull();
        jdbcLinkRepository.updateContent(idLink, exceptedContent).block();
        var actualContent = jdbcLinkRepository.findById(idLink).block().getContent();

        assertThat(exceptedContent).isEqualTo(actualContent);
    }

    @DynamicPropertySource
    static void jdbcProperties(DynamicPropertyRegistry registry) {
        registry.add("app.database-access-type", () -> "jooq");
    }
}
