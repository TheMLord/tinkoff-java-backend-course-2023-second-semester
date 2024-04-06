package edu.java.configuration;

import edu.java.configuration.dataAccess.AccessType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @Bean
    @NotNull
    Scheduler scheduler,
    @NotNull ClientBaseUrl clientBaseUrl,

    @NotNull
    AccessType databaseAccessType,
    @NotNull Boolean useQueue,
    @NotNull
    Kafka kafka
) {

    public record Scheduler(boolean enable, @NotNull Duration interval, @NotNull Duration forceCheckDelay) {
    }

    public record ClientBaseUrl(@NotNull @NotEmpty String githubUri, @NotNull @NotEmpty String stackoverflowUri,
                                @NotNull @NotEmpty String botUrl) {
    }

    public record Kafka(@NotBlank String bootstrapServers,
                        @NotNull @NotBlank String updateTopicName,
                        @NotNull @NotEmpty TopicsProperty[] topicsProperty) {
    }

    public record TopicsProperty(@NotBlank String topicName,
                                 @NotNull @Positive Integer numberPartitions,
                                 @NotNull @Positive Short replicationFactor) {

    }
}
