package org.example.age.common.client.redis;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import redis.clients.jedis.JedisPooled;

/**
 * Dagger module that binds {@link JedisPooled}.
 * <p>
 * Depends on an unbound {@link RedisClientConfig}.
 */
@Module
public abstract class RedisClientModule {

    @Provides
    @Singleton
    static JedisPooled bindJedisPooled(RedisClientConfig config) {
        return new JedisPooled(config.url().toString());
    }

    RedisClientModule() {}
}
