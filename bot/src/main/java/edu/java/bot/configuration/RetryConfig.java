package edu.java.bot.configuration;

import edu.java.bot.exceptions.ScrapperApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import reactor.util.retry.Retry;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class RetryConfig {
    private final ApplicationConfig applicationConfig;

    @Bean
    public Retry retryPolicy() {
        var maxAttempts = applicationConfig.retry().maxAttempts();
        var delay = applicationConfig.retry().delay();

        var retry = switch (applicationConfig.retry().backOffPolicy()) {
            case CONSTANT -> Retry.fixedDelay(maxAttempts, delay);
            case LINEAR -> Retry.fixedDelay(maxAttempts, delay);
            case EXPONENTIAL -> Retry.backoff(maxAttempts, delay);
        };

        return retry
            .filter(throwable -> {
                if (throwable instanceof ScrapperApiException apiException) {
                    var code = HttpStatus.valueOf(Integer.parseInt(apiException.getApiErrorResponse().getCode()));
                    return applicationConfig.retry().httpStatuses().contains(code);
                }
                return false;
            }).onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                log.info("recover call method after the end of the attempts");
                if (retrySignal.failure() instanceof ScrapperApiException scrapperApiException) {
                    return scrapperApiException;
                }
                return retrySignal.failure();
            });
    }
}
