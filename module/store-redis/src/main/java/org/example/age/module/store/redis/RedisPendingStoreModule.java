package org.example.age.module.store.redis;

import dagger.Binds;
import dagger.Module;
import org.example.age.module.common.CommonModule;
import org.example.age.module.common.LiteEnv;
import org.example.age.module.store.redis.client.RedisClientModule;
import org.example.age.module.store.redis.client.RedisConfig;
import org.example.age.service.module.store.PendingStoreRepository;

/**
 * Dagger module that binds {@link PendingStoreRepository}.
 * <p>
 * Depends on an unbound...
 * <ul>
 *     <li>{@link RedisConfig}
 *     <li>{@link LiteEnv}
 * </ul>
 */
@Module(includes = {RedisClientModule.class, CommonModule.class})
public interface RedisPendingStoreModule {

    @Binds
    PendingStoreRepository bindPendingStoreRepository(RedisPendingStoreRepository impl);
}
