package edu.java.bot.configuration.retry;

import java.time.Duration;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

/**
 * Linear retry policy
 */
public final class LinearRetryPolicy extends Retry {
    private final Duration backOff;
    private final long maxAttempts;
    private final Predicate<Throwable> errorFilter;
    private final BiFunction<LinearRetryPolicy, Retry.RetrySignal, Throwable> retryExhaustedGenerator;

    public LinearRetryPolicy(Duration backOff, long max) {
        this.backOff = backOff;
        this.maxAttempts = max;
        this.errorFilter = null;
        this.retryExhaustedGenerator = null;
    }

    private LinearRetryPolicy(
        Duration backOff,
        long max,
        Predicate<? super Throwable> aThrowablePredicate,
        BiFunction<LinearRetryPolicy, Retry.RetrySignal, Throwable> retryExhaustedGenerator
    ) {
        this.backOff = backOff;
        this.maxAttempts = max;
        this.errorFilter = aThrowablePredicate::test;
        this.retryExhaustedGenerator = retryExhaustedGenerator;
    }

    public LinearRetryPolicy filter(Predicate<? super Throwable> errorFilter) {
        return new LinearRetryPolicy(
            this.backOff,
            this.maxAttempts,
            (Predicate) Objects.requireNonNull(errorFilter, "errorFilter"),
            this.retryExhaustedGenerator
        );
    }

    public LinearRetryPolicy onRetryExhaustedThrow(
        BiFunction<LinearRetryPolicy, RetrySignal, Throwable> retryExhaustedGenerator
    ) {
        return new LinearRetryPolicy(
            this.backOff,
            this.maxAttempts,
            this.errorFilter,
            (BiFunction) Objects.requireNonNull(
                retryExhaustedGenerator,
                "retryExhaustedGenerator"
            )
        );
    }

    public Flux<Long> generateCompanion(Flux<Retry.RetrySignal> t) {
        return Flux.deferContextual((cv) -> {
            return t.contextWrite(cv).concatMap((retryWhenState) -> {
                var copy = retryWhenState.copy();
                Throwable currentFailure = copy.failure();
                long iteration = copy.totalRetries();
                if (currentFailure == null) {
                    return Mono.error(new IllegalStateException("Retry.RetrySignal#failure() not expected to be null"));
                } else if (!this.errorFilter.test(currentFailure)) {
                    return Mono.error(currentFailure);
                } else if (iteration >= this.maxAttempts) {
                    return Mono.error((Throwable) this.retryExhaustedGenerator.apply(this, copy));
                } else {
                    return Mono.delay(Duration.ofSeconds(backOff.getSeconds() * (copy.totalRetries() + 1)))
                        .thenReturn(copy.totalRetries());
                }
            }).onErrorStop();
        });
    }
}
