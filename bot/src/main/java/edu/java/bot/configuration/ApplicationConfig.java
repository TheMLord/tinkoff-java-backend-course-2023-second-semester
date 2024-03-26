package edu.java.bot.configuration;

import edu.java.bot.configuration.retry.BackOffPolicy;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    @NotEmpty
    String telegramToken,

    @NotEmpty
    String scrapperBaseUri,

    @NotNull
    Retry retry
) {
    public record Retry(@NotNull BackOffPolicy backOffPolicy, @NotEmpty Set<HttpStatus> httpStatuses,
                        @NotNull Integer maxAttempts,
                        @NotNull Duration delay) {

    }
}
