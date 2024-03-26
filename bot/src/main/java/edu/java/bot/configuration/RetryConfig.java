package edu.java.bot.configuration;

import edu.java.bot.configuration.retry.LinearBackOffPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@RequiredArgsConstructor
public class RetryConfig {
    private final ApplicationConfig applicationConfig;

    @Bean
    BackOffPolicy backOffPolicy() {
        var delay = applicationConfig.retry().delay();
        return switch (applicationConfig.retry().backOffPolicy()) {
            case CONSTANT -> {
                var constantBackOff = new FixedBackOffPolicy();
                constantBackOff.setBackOffPeriod(delay.toMillis());
                yield constantBackOff;
            }
            case LINEAR -> {
                var linearBackOffPolicy = new LinearBackOffPolicy();
                linearBackOffPolicy.setBackOffPeriod(delay.toMillis());
                yield linearBackOffPolicy;
            }
            case EXPONENTIAL -> {
                var exponentialBackOff = new ExponentialBackOffPolicy();
                exponentialBackOff.setInitialInterval(delay.toMillis());
                yield exponentialBackOff;
            }
        };
    }

    @Bean
    RetryPolicy retryPolicy() {
        var retryPolice = new SimpleRetryPolicy();
        retryPolice.setMaxAttempts(applicationConfig.retry().maxAttempts());
        return retryPolice;
    }

    @Bean
    RetryTemplate retryTemplate(BackOffPolicy backOffPolicy, RetryPolicy retryPolicy) {
        var retryTemplate = new RetryTemplate();
        retryTemplate.setBackOffPolicy(backOffPolicy);
        retryTemplate.setRetryPolicy(retryPolicy);
        return retryTemplate;
    }
}
