package org.example.age.module.common.testing;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.example.age.common.testing.TestObjectMapper;
import org.example.age.module.common.LiteEnv;

/** Test implementation of {@link LiteEnv}. The worker has a single thread. */
@Singleton
final class TestLiteEnv implements LiteEnv {

    private final ExecutorService worker = Executors.newFixedThreadPool(1);
    private final TestProviderRegistrar providerRegistrar;

    @Inject
    public TestLiteEnv(Optional<TestProviderRegistrar> maybeProviderRegistrar) {
        providerRegistrar = maybeProviderRegistrar.orElse(provider -> {});
    }

    @Override
    public ObjectMapper jsonMapper() {
        return TestObjectMapper.get();
    }

    @Override
    public ExecutorService worker() {
        return worker;
    }

    @Override
    public void registerProvider(Object provider) {
        providerRegistrar.register(provider);
    }
}
