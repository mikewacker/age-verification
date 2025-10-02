package org.example.age.common.app.env;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.util.Duration;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.concurrent.ExecutorService;
import org.example.age.common.env.LiteEnv;

/** Implementation of {@link LiteEnv} for Dropwizard. */
@Singleton
final class DropwizardLiteEnv implements LiteEnv {

    private final ObjectMapper mapper;
    private final ExecutorService worker;

    @Inject
    public DropwizardLiteEnv(Environment env) {
        mapper = env.getObjectMapper();
        worker = createWorker(env);
    }

    @Override
    public ObjectMapper jsonMapper() {
        return mapper;
    }

    @Override
    public ExecutorService worker() {
        return worker;
    }

    /** Creates the thread pool for the worker. */
    public ExecutorService createWorker(Environment env) {
        int size = availableProcessors() * 8;
        return env.lifecycle()
                .executorService("worker")
                .maxThreads(size)
                .minThreads(size)
                .shutdownTime(Duration.milliseconds(1))
                .build();
    }

    /** Get the number of processors, or 2 if only one processor exists. */
    private static int availableProcessors() {
        return Math.max(Runtime.getRuntime().availableProcessors(), 2);
    }
}
