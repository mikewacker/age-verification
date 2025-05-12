package org.example.age.module.store.redis.testing;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.util.concurrent.ExecutorService;
import org.example.age.module.store.redis.client.RedisConfig;
import org.example.age.testing.TestEnvModule;

/**
 * Dagger module that binds...
 * <ul>
 *     <li>{@link RedisConfig}
 *     <li>{@link ObjectMapper}
 *     <li><code>@Named {@link ExecutorService}</code>
 * </ul>
 * <p>
 * Depends on an unbound <code>@Named("port") int</code>.
 */
@Module(includes = TestEnvModule.class)
public interface TestDependenciesModule {

    @Provides
    @Singleton
    static RedisConfig provideRedisConfig(@Named("port") int port) {
        return TestConfig.createRedis(port);
    }
}
