package edu.java.scrapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class StartContainerTest extends IntegrationEnvironment {
    @Test
    @DisplayName("test that the container is running successfully")
    void testThatTheContainerIsRunningSuccessfully() {
        assertThat(true).isTrue();
    }

}
