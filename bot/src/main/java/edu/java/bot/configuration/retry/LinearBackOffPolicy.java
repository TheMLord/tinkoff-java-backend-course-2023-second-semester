package edu.java.bot.configuration.retry;

import java.util.concurrent.atomic.AtomicLong;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.retry.backoff.BackOffInterruptedException;
import org.springframework.retry.backoff.Sleeper;
import org.springframework.retry.backoff.SleepingBackOffPolicy;
import org.springframework.retry.backoff.StatelessBackOffPolicy;
import org.springframework.retry.backoff.ThreadWaitSleeper;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class LinearBackOffPolicy extends StatelessBackOffPolicy implements SleepingBackOffPolicy<LinearBackOffPolicy> {

    private static final long DEFAULT_BACK_OFF_PERIOD = 1000L;

    private AtomicLong backOffPeriod = new AtomicLong(DEFAULT_BACK_OFF_PERIOD);

    private AtomicLong backOffDelta = new AtomicLong(DEFAULT_BACK_OFF_PERIOD);

    private Sleeper sleeper = new ThreadWaitSleeper();

    @Override
    public LinearBackOffPolicy withSleeper(Sleeper sleeper) {
        LinearBackOffPolicy linearBackoffPolicy = new LinearBackOffPolicy();
        linearBackoffPolicy.setBackOffPeriod(backOffPeriod.get());
        linearBackoffPolicy.setSleeper(sleeper);
        return linearBackoffPolicy;
    }

    public void setBackOffPeriod(long backOffPeriod) {
        this.backOffPeriod.set(backOffPeriod > 0 ? backOffPeriod : DEFAULT_BACK_OFF_PERIOD);
        this.backOffDelta.set(backOffPeriod);
    }

    protected void doBackOff() throws BackOffInterruptedException {
        try {
            sleeper.sleep(backOffPeriod.get());
            this.backOffPeriod.set(backOffPeriod.get() + backOffDelta.get());
        } catch (InterruptedException e) {
            throw new BackOffInterruptedException("Thread interrupted while sleeping", e);
        }
    }

}
