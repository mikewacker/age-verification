package org.example.age.module.store.redis.testing;

import dagger.Module;
import dagger.Provides;
import org.example.age.common.env.LiteEnv;
import org.example.age.module.store.redis.client.RedisConfig;
import org.example.age.testing.env.TestLiteEnvModule;

/**
 * Dagger module that binds...
 * <ul>
 *     <li>{@link RedisConfig}
 *     <li>{@link LiteEnv}.
 * </ul>
 */
@Module(includes = TestLiteEnvModule.class)
public interface TestDependenciesModule {

    @Provides
    static RedisConfig provideRedisConfig() {
        return TestConfig.redis();
    }
}
