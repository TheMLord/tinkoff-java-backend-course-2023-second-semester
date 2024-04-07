package edu.java.bot.configuration;

import edu.java.bot.configuration.retry.BackOffPolicy;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Duration;
import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;

/**
 * Application configuration class with the app prefix.
 *
 * @param telegramToken telegram bot token for accessing the HTTP API.
 */
@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @NotEmpty String telegramToken,

    @NotEmpty String scrapperBaseUri,
    @NotNull Retry retry,
    @NotNull Kafka kafka
) {
    public record Retry(@NotNull BackOffPolicy backOffPolicy, @NotEmpty Set<HttpStatus> httpStatuses,
                        @NotNull Integer maxAttempts,
                        @NotNull Duration delay) {

    }

    public record Kafka(@NotBlank String bootstrapServers,
                        @NotNull @NotBlank String updateTopicName,
                        @NotNull @NotBlank String dlqTopicName,
                        @NotNull @NotEmpty TopicsProperty[] topicsProperty) {
    }

    public record TopicsProperty(@NotBlank String topicName,
                                 @NotNull @Positive Integer numberPartitions,
                                 @NotNull @Positive Short replicationFactor) {

    }
}
