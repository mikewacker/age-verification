package org.example.age.module.store.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import java.util.concurrent.ExecutorService;
import org.example.age.service.module.store.PendingStoreRepository;
import redis.clients.jedis.JedisPooled;

/**
 * Dagger module that binds {@link PendingStoreRepository}.
 * <p>
 * Depends on an unbound...
 * <ul>
 *     <li>{@link RedisConfig}
 *     <li>{@link ObjectMapper}
 *     <li><code>@Named {@link ExecutorService}</code>
 * </ul>
 * <p>
 * Requires sticky sessions to work in a distributed environment.
 */
@Module
public interface RedisPendingStoreModule {

    @Binds
    PendingStoreRepository bindPendingStoreRepository(RedisPendingStoreRepository impl);

    @Provides
    @Singleton
    static JedisPooled bindJedisPooled(RedisConfig config) {
        return new JedisPooled(config.url().toString());
    }
}
