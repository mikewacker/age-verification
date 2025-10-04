package org.example.age.module.store.redis.testing;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import org.example.age.common.client.redis.RedisClientConfig;
import org.example.age.common.env.LiteEnv;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.env.TestEnvModule;

/**
 * Dagger module that binds...
 * <ul>
 *     <li>{@link RedisClientConfig}
 *     <li>{@link LiteEnv}.
 * </ul>
 */
@Module(includes = TestEnvModule.class)
public interface TestDependenciesModule {

    @Provides
    @Singleton
    static RedisClientConfig provideRedisConfig() {
        return RedisClientConfig.builder().url(TestClient.localhostUrl(6379)).build();
    }
}
