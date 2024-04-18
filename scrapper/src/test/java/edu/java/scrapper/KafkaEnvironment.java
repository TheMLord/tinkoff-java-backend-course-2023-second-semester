package edu.java.scrapper;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import static edu.java.scrapper.IntegrationEnvironment.POSTGRES;

@Testcontainers
public abstract class KafkaEnvironment {
    public static KafkaContainer kafkaContainer;

    static {
        kafkaContainer = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.3.3")
        );
    }

    @DynamicPropertySource
    private static void kafkaQueueSetUp(DynamicPropertyRegistry dpr) {
        dpr.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        dpr.add("spring.datasource.username", POSTGRES::getUsername);
        dpr.add("spring.datasource.password", POSTGRES::getPassword);
        dpr.add("spring.liquibase.enabled", () -> false);
        dpr.add("app.useQueue", () -> true);
    }
}
