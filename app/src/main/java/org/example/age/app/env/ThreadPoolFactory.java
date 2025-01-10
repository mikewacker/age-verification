package org.example.age.app.env;

import io.dropwizard.core.setup.Environment;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

/** Factory for thread pools. */
@Singleton
final class ThreadPoolFactory {

    private static final int numProcessors = availableProcessors();

    private final Environment env;

    @Inject
    public ThreadPoolFactory(Environment env) {
        this.env = env;
    }

    /** Creates the thread pool for the worker. */
    public ExecutorService createWorker() {
        int size = numProcessors * 8;
        return env.lifecycle()
                .executorService("worker")
                .maxThreads(size)
                .minThreads(size)
                .build();
    }

    /** Creates the thread pool for scheduled tasks. */
    public ScheduledExecutorService createScheduled() {
        int size = numProcessors;
        return env.lifecycle()
                .scheduledExecutorService("scheduled")
                .threads(size)
                .build();
    }

    /** Get the number of processors, or 2 if only one processor exists. */
    private static int availableProcessors() {
        return Math.max(Runtime.getRuntime().availableProcessors(), 2);
    }
}
