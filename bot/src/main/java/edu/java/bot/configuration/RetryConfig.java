package edu.java.bot.configuration;

import edu.java.bot.configuration.retry.LinearRetryPolicy;
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
    private static final String RETRY_MESSAGE = "recover call method after the end of the attempts";

    @Bean
    public Retry retryPolicy() {
        var maxAttempts = applicationConfig.retry().maxAttempts();
        var delay = applicationConfig.retry().delay();

        return switch (applicationConfig.retry().backOffPolicy()) {
            case CONSTANT -> Retry.fixedDelay(maxAttempts, delay)
                .filter(throwable -> {
                        if (throwable instanceof ScrapperApiException apiException) {
                            var code = HttpStatus.valueOf(
                                Integer.parseInt(apiException.getApiErrorResponse().getCode())
                            );
                            return applicationConfig.retry().httpStatuses().contains(code);
                        }
                        return false;
                    }
                ).onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                        log.info(RETRY_MESSAGE);
                        if (retrySignal.failure() instanceof ScrapperApiException scrapperApiException) {
                            return scrapperApiException;
                        }
                        return retrySignal.failure();
                    }
                );
            case LINEAR -> {
                var retry = new LinearRetryPolicy(delay, maxAttempts);
                yield retry.filter(throwable -> {
                        if (throwable instanceof ScrapperApiException apiException) {
                            var code = HttpStatus.valueOf(
                                Integer.parseInt(apiException.getApiErrorResponse().getCode())
                            );
                            return applicationConfig.retry().httpStatuses().contains(code);
                        }
                        return false;
                    }
                ).onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                        log.info(RETRY_MESSAGE);
                        if (retrySignal.failure() instanceof ScrapperApiException scrapperApiException) {
                            return scrapperApiException;
                        }
                        return retrySignal.failure();
                    }
                );
            }
            case EXPONENTIAL -> Retry.backoff(maxAttempts, delay)
                .filter(throwable -> {
                        if (throwable instanceof ScrapperApiException apiException) {
                            var code = HttpStatus.valueOf(
                                Integer.parseInt(apiException.getApiErrorResponse().getCode())
                            );
                            return applicationConfig.retry().httpStatuses().contains(code);
                        }
                        return false;
                    }
                ).onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                        log.info(RETRY_MESSAGE);
                        if (retrySignal.failure() instanceof ScrapperApiException scrapperApiException) {
                            return scrapperApiException;
                        }
                        return retrySignal.failure();
                    }
                );
        };
    }
}
