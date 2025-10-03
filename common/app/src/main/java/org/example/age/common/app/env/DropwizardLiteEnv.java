package org.example.age.common.app.env;

import com.fasterxml.jackson.annotation.JsonInclude;
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
        mapper = getAndConfigureJsonMapper(env);
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

    /** Gets and configures the JSON object mapper. */
    private static ObjectMapper getAndConfigureJsonMapper(Environment env) {
        ObjectMapper mapper = env.getObjectMapper();
        mapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }

    /** Creates the worker thread pool. */
    private static ExecutorService createWorker(Environment env) {
        int size = Math.max(Runtime.getRuntime().availableProcessors(), 2) * 8;
        return env.lifecycle()
                .executorService("worker")
                .maxThreads(size)
                .minThreads(size)
                .shutdownTime(Duration.milliseconds(1))
                .build();
    }
}
