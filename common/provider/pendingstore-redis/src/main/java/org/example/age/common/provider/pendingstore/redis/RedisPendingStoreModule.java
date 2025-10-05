package org.example.age.common.provider.pendingstore.redis;

import dagger.Binds;
import dagger.Module;
import org.example.age.common.client.redis.RedisClientConfig;
import org.example.age.common.client.redis.RedisClientModule;
import org.example.age.common.env.BaseEnvModule;
import org.example.age.common.env.LiteEnv;
import org.example.age.common.spi.PendingStoreRepository;

/**
 * Dagger module that binds {@link PendingStoreRepository}.
 * <p>
 * Depends on an unbound...
 * <ul>
 *     <li>{@link RedisClientConfig}
 *     <li>{@link LiteEnv}
 * </ul>
 */
@Module(includes = {RedisClientModule.class, BaseEnvModule.class})
public abstract class RedisPendingStoreModule {

    @Binds
    abstract PendingStoreRepository bindPendingStoreRepository(RedisPendingStoreRepository impl);

    RedisPendingStoreModule() {}
}
