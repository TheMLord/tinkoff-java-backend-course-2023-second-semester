package edu.java.scrapper;

import java.util.List;
import edu.java.schedulers.LinkUpdaterScheduler;
import org.apache.kafka.clients.admin.AdminClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.annotation.DirtiesContext;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext
public class StartContainerTest extends IntegrationEnvironment {
    @MockBean AdminClient adminClient;

    @Autowired JdbcClient jdbcClient;
    @MockBean LinkUpdaterScheduler linkUpdaterScheduler;

    @Test
    @DisplayName("test that the container is running successfully")
    void testThatTheContainerIsRunningSuccessfully() {
        var exceptedTableTgChats = "tgchats";
        var exceptedTableNameLinks = "links";
        var exceptedTableNameSubscriptions = "subscriptions";

        List<String> tableNames = jdbcClient.sql(
                "SELECT table_name FROM information_schema.tables " +
                    "WHERE table_schema='public' " +
                    "AND table_catalog = current_database()")
            .query()
            .singleColumn().stream().map(Object::toString).toList();

        assertThat(tableNames).containsAll(List.of(
                exceptedTableTgChats,
                exceptedTableNameLinks,
                exceptedTableNameSubscriptions
            )
        );

    }
}
