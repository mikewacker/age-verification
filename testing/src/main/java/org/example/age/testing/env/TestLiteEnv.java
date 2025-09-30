package org.example.age.testing.env;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.example.age.module.common.LiteEnv;
import org.example.age.testing.util.TestObjectMapper;

/** Test implementation of {@link LiteEnv}. The worker has a single thread. */
@Singleton
final class TestLiteEnv implements LiteEnv {

    private final ExecutorService worker = Executors.newFixedThreadPool(1);

    @Inject
    public TestLiteEnv() {}

    @Override
    public ObjectMapper jsonMapper() {
        return TestObjectMapper.get();
    }

    @Override
    public ExecutorService worker() {
        return worker;
    }
}
