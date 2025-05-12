package org.example.age.module.store.redis.client;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import redis.clients.jedis.JedisPooled;

/**
 * Dagger module that binds {@link JedisPooled}.
 * <p>
 * Depends on an unbound {@link RedisConfig}.
 */
@Module
public interface RedisClientModule {

    @Provides
    @Singleton
    static JedisPooled bindJedisPooled(RedisConfig config) {
        return new JedisPooled(config.url().toString());
    }
}
