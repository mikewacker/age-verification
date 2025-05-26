package org.example.age.module.store.redis.testing;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
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
 */
@Module(includes = TestEnvModule.class)
public interface TestDependenciesModule {

    @Provides
    static RedisConfig provideRedisConfig() {
        return TestConfig.redis();
    }
}
