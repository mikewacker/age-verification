package org.example.age.api;

import java.time.Duration;
import java.util.concurrent.Executor;

/** Executor that can also schedule cancellable tasks. */
public interface ScheduledExecutor extends Executor {

    /** Executes the command after a delay, returning a task key that can cancel the task. */
    Key executeAfter(Runnable command, Duration delay);

    /** Task key that can cancel the task. */
    @FunctionalInterface
    interface Key {

        /**
         * Tries to cancel the task, returning true if the task was cancelled,
         * or false if the task has already run or was previously cancelled.
         */
        boolean cancel();
    }
}
