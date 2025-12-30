package org.example.age.common.client.redis;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import redis.clients.jedis.RedisClient;

/**
 * Dagger module that binds {@link RedisClient}.
 * <p>
 * Depends on an unbound {@link RedisClientConfig}.
 */
@Module
public abstract class RedisClientModule {

    @Provides
    @Singleton
    static RedisClient bindRedisClient(RedisClientConfig config) {
        return RedisClient.create(config.uri());
    }

    RedisClientModule() {}
}
