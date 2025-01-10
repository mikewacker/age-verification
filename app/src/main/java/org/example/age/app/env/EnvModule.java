package org.example.age.app.env;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import io.dropwizard.core.setup.Environment;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Dagger modules that binds...
 * <ul>
 *     <li>{@link ObjectMapper}
 *     <li><code>@named("worker") {@link ExecutorService}</code>
 *     <li>{@link ScheduledExecutorService}
 * </ul>
 * <p>
 * Depends on an unbound {@link Environment}.
 */
@Module
public interface EnvModule {

    @Provides
    static ObjectMapper provideObjectMapper(Environment env) {
        return env.getObjectMapper();
    }

    @Provides
    @Named("worker")
    @Singleton
    static ExecutorService provideWorkerThreadPool(ThreadPoolFactory threadPoolFactory) {
        return threadPoolFactory.createWorker();
    }

    @Provides
    @Singleton
    static ScheduledExecutorService provideScheduledThreadPool(ThreadPoolFactory threadPoolFactory) {
        return threadPoolFactory.createScheduled();
    }
}
