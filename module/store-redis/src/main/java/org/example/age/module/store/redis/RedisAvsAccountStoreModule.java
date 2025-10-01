package org.example.age.module.store.redis;

import dagger.Binds;
import dagger.Module;
import org.example.age.common.env.EnvModule;
import org.example.age.common.env.LiteEnv;
import org.example.age.module.store.redis.client.RedisClientModule;
import org.example.age.module.store.redis.client.RedisConfig;
import org.example.age.service.module.store.AvsVerifiedUserStore;

/**
 * Dagger module that binds {@link AvsVerifiedUserStore}.
 * <p>
 * Depends on an unbound...
 * <ul>
 *     <li>{@link RedisConfig}
 *     <li>{@link LiteEnv}
 * </ul>
 */
@Module(includes = {RedisClientModule.class, EnvModule.class})
public interface RedisAvsAccountStoreModule {

    @Binds
    AvsVerifiedUserStore bindAvsVerifiedUserStore(RedisAvsVerifiedUserStore impl);
}
