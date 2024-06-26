package edu.java.scrapper.repository.jdbc;

import edu.java.exceptions.DoubleRegistrationException;
import edu.java.exceptions.NotExistTgChatException;
import edu.java.repository.TgChatRepository;
import edu.java.scrapper.IntegrationEnvironment;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@DirtiesContext
@TestPropertySource(locations = "classpath:test")
@Sql(value = "classpath:sql/clearDB.sql",
     executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
public class JdbcTgChatRepositoryTest extends IntegrationEnvironment {
    @MockBean AdminClient adminClient;
    @MockBean KafkaAdmin kafkaAdmin;

    @Autowired TgChatRepository jdbcTgChatRepository;

    @Test
    @DisplayName("Test that the chat is being added successfully returned the chat with the correct id")
    @Transactional
    @Rollback
    void testThatTheChatIsBeingAddedSuccessfullyReturnedTheChatWithTheCorrectId() {
        var exceptedId = 30L;

        jdbcTgChatRepository.add(exceptedId).block();
        var actualChatEntity = jdbcTgChatRepository.findById(exceptedId).block();

        assertThat(actualChatEntity).isNotNull();
        assertThat(actualChatEntity.getId()).isEqualTo(exceptedId);
    }

    @Test
    @DisplayName("Test that the chat cannot be registered twice and returned a correct error")
    @Transactional
    @Rollback
    void testThatTheChatCannotBeRegisteredTwiceAndReturnedACorrectError() {
        var existChat1 = 31L;

        jdbcTgChatRepository.add(existChat1).block();

        assertThatThrownBy(() -> jdbcTgChatRepository.add(existChat1).block())
            .isInstanceOf(DoubleRegistrationException.class);
    }

    @Test
    @DisplayName("Test that the chat is being deleted successfully")
    @Transactional
    @Rollback
    void testThatTheChatIsBeingDeletedSuccessfully() {
        var idChatToDelete = 32L;

        jdbcTgChatRepository.add(idChatToDelete).block();
        assertThat(jdbcTgChatRepository.findById(idChatToDelete).block()).isNotNull();

        jdbcTgChatRepository.remove(idChatToDelete).block();
        assertThatThrownBy(() -> jdbcTgChatRepository.findById(idChatToDelete).block()).isInstanceOf(
            NotExistTgChatException.class);
    }

    @Test
    @DisplayName("Test that it is impossible to delete a non-existent chat and returned the correct error")
    @Transactional
    @Rollback
    void testThatItIsImpossibleToDeleteANonExistentChatAndReturnedTheCorrectError() {
        var idChatToDelete = 33L;

        assertThatThrownBy(() -> jdbcTgChatRepository.findById(idChatToDelete).block())
            .isInstanceOf(NotExistTgChatException.class);

        assertThatThrownBy(() -> jdbcTgChatRepository.remove(idChatToDelete).block())
            .isInstanceOf(NotExistTgChatException.class);
    }

    @DynamicPropertySource
    static void jdbcProperties(DynamicPropertyRegistry registry) {
        registry.add("app.database-access-type", () -> "jdbc");
    }
}
