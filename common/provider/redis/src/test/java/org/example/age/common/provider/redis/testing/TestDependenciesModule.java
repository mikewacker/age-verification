package org.example.age.common.provider.redis.testing;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import org.example.age.common.provider.redis.RedisConfig;
import org.example.age.testing.client.TestClient;

@Module
public interface TestDependenciesModule {

    @Provides
    @Singleton
    static RedisConfig provideRedisConfig() {
        return RedisConfig.builder().url(TestClient.localhostUrl(6379)).build();
    }
}
