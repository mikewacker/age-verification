package org.example.age.testing.api;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.example.age.api.base.ScheduledExecutor;

/** Fake same-thread {@link ScheduledExecutor} where scheduled tasks must be run manually. */
public final class FakeScheduledExecutor implements ScheduledExecutor {

    private final AtomicReference<ScheduledTask> lastScheduledTask = new AtomicReference<>(null);

    /** Creates a {@link FakeScheduledExecutor}. */
    public static FakeScheduledExecutor create() {
        return new FakeScheduledExecutor();
    }

    /** Gets the last scheduled task. */
    public ScheduledTask getLastScheduledTask() {
        return Objects.requireNonNull(lastScheduledTask.get());
    }

    @Override
    public void execute(Runnable command) {
        command.run();
    }

    @Override
    public Key executeAfter(Runnable command, Duration delay) {
        ScheduledTask scheduledTask = new ScheduledTask(command, delay);
        lastScheduledTask.set(scheduledTask);
        return scheduledTask;
    }

    private FakeScheduledExecutor() {}

    /** Scheduled task that must be run manually. */
    public static final class ScheduledTask implements Runnable, Key {

        private final Runnable command;
        private final Duration delay;

        private final AtomicBoolean canRun = new AtomicBoolean(true);

        /** Gets the delay before the task is run. */
        public Duration getDelay() {
            return delay;
        }

        @Override
        public void run() {
            if (!canRun.getAndSet(false)) {
                return;
            }

            command.run();
        }

        @Override
        public boolean cancel() {
            return canRun.getAndSet(false);
        }

        private ScheduledTask(Runnable command, Duration delay) {
            this.command = command;
            this.delay = delay;
        }
    }
}
