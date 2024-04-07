package edu.java.scrapper.services.jpa;

import edu.java.schedulers.LinkUpdaterScheduler;
import edu.java.scrapper.IntegrationEnvironment;
import edu.java.services.ChatService;
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

@SpringBootTest
@DirtiesContext
@Sql(value = "classpath:sql/service-insert-test.sql",
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = "classpath:sql/clearDB.sql",
     executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@TestPropertySource(locations = "classpath:test")
public class JpaTgChatServiceTest extends IntegrationEnvironment {
    @MockBean AdminClient adminClient;
    @MockBean KafkaAdmin kafkaAdmin;

    @MockBean LinkUpdaterScheduler linkUpdaterScheduler;
    @Autowired ChatService chatService;

    @Test
    @DisplayName("Test that the service does not return errors when adding a new chat correctly")
    @Transactional
    @Rollback
    void testThatTheServiceDoesNotReturnErrorsWhenAddingANewChatCorrectly() {
        var newChatId = 4L;

        chatService.register(newChatId).block();
    }

    @Test
    @DisplayName("Test that the service does not return errors if the chat is deleted correctly")
    @Transactional
    @Rollback
    void testThatTheServiceDoesNotReturnErrorsIfTheChatIsDeletedCorrectly() {
        var existChat = 1L;

        chatService.unRegister(existChat).block();
    }

    @DynamicPropertySource
    static void jdbcProperties(DynamicPropertyRegistry registry) {
        registry.add("app.database-access-type", () -> "jpa");
    }
}
