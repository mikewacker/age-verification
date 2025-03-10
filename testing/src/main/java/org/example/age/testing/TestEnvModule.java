package org.example.age.testing;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Dagger module that binds...
 * <ul>
 *     <li>{@link ObjectMapper}
 *     <li><code>@Named("worker") {@link ExecutorService}</code>
 * </ul>
 * <p>
 * Thread pools have a single thread.
 */
@Module
public interface TestEnvModule {

    @Provides
    @Singleton
    static ObjectMapper provideObjectMapper() {
        return TestObjectMapper.get();
    }

    @Provides
    @Named("worker")
    @Singleton
    static ExecutorService provideWorkerThreadPool() {
        return Executors.newFixedThreadPool(1);
    }
}
