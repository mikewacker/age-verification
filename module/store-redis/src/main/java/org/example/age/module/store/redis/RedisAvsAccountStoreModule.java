package org.example.age.module.store.redis;

import dagger.Binds;
import dagger.Module;
import org.example.age.avs.spi.AvsVerifiedUserStore;
import org.example.age.common.env.BaseEnvModule;
import org.example.age.common.env.LiteEnv;
import org.example.age.module.store.redis.client.RedisClientModule;
import org.example.age.module.store.redis.client.RedisConfig;

/**
 * Dagger module that binds {@link AvsVerifiedUserStore}.
 * <p>
 * Depends on an unbound...
 * <ul>
 *     <li>{@link RedisConfig}
 *     <li>{@link LiteEnv}
 * </ul>
 */
@Module(includes = {RedisClientModule.class, BaseEnvModule.class})
public interface RedisAvsAccountStoreModule {

    @Binds
    AvsVerifiedUserStore bindAvsVerifiedUserStore(RedisAvsVerifiedUserStore impl);
}
