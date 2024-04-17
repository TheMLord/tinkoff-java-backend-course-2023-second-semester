package edu.java.scrapper.services.jdbc;

import edu.java.exceptions.NotExistTgChatException;
import edu.java.repository.TgChatRepository;
import edu.java.scrapper.IntegrationEnvironment;
import edu.java.services.ChatService;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@DirtiesContext
@Sql(value = "classpath:sql/jdbcservice-insert-test.sql",
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = "classpath:sql/clearDB.sql",
     executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@TestPropertySource(locations = "classpath:test")
public class JdbcTgChatServiceTest extends IntegrationEnvironment {
    @Autowired ChatService jdbcChatService;
    @Autowired TgChatRepository jdbcTgChatRepository;

    @Test
    @DisplayName("Test that the service does not return errors when adding a new chat correctly")
    @Transactional
    @Rollback
    void testThatTheServiceDoesNotReturnErrorsWhenAddingANewChatCorrectly() {
        var newChatId = 4L;

        assertThatThrownBy(() -> jdbcTgChatRepository.findById(newChatId).block())
            .isInstanceOf(NotExistTgChatException.class);
        jdbcChatService.register(newChatId).block();
        assertThat(jdbcTgChatRepository.findById(newChatId).block()).isNotNull();
    }

    @Test
    @DisplayName("Test that the service does not return errors if the chat is deleted correctly")
    @Transactional
    @Rollback
    void testThatTheServiceDoesNotReturnErrorsIfTheChatIsDeletedCorrectly() {
        var existChat = 1L;

        assertThat(jdbcTgChatRepository.findById(existChat).block()).isNotNull();
        jdbcChatService.unRegister(existChat).block();
        assertThatThrownBy(() -> jdbcTgChatRepository.findById(existChat).block())
            .isInstanceOf(NotExistTgChatException.class);
    }

    @DynamicPropertySource
    static void jdbcProperties(DynamicPropertyRegistry registry) {
        registry.add("app.database-access-type", () -> "jdbc");
    }
}
