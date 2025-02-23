package org.example.age.app.env;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import io.dropwizard.core.setup.Environment;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.util.concurrent.ExecutorService;

/**
 * Dagger module that binds...
 * <ul>
 *     <li>{@link ObjectMapper}
 *     <li><code>@Named("worker") {@link ExecutorService}</code>
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
}
