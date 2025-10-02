package org.example.age.module.store.redis;

import dagger.Binds;
import dagger.Module;
import org.example.age.common.env.EnvModule;
import org.example.age.common.env.LiteEnv;
import org.example.age.common.spi.PendingStoreRepository;
import org.example.age.module.store.redis.client.RedisClientModule;
import org.example.age.module.store.redis.client.RedisConfig;

/**
 * Dagger module that binds {@link PendingStoreRepository}.
 * <p>
 * Depends on an unbound...
 * <ul>
 *     <li>{@link RedisConfig}
 *     <li>{@link LiteEnv}
 * </ul>
 */
@Module(includes = {RedisClientModule.class, EnvModule.class})
public interface RedisPendingStoreModule {

    @Binds
    PendingStoreRepository bindPendingStoreRepository(RedisPendingStoreRepository impl);
}
