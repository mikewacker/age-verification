package org.example.age.avs.provider.accountstore.redis;

import dagger.Binds;
import dagger.Module;
import org.example.age.avs.spi.AvsVerifiedAccountStore;
import org.example.age.common.client.redis.RedisClientConfig;
import org.example.age.common.client.redis.RedisClientModule;
import org.example.age.common.env.BaseEnvModule;
import org.example.age.common.env.LiteEnv;

/**
 * Dagger module that binds {@link AvsVerifiedAccountStore}.
 * <p>
 * Depends on an unbound...
 * <ul>
 *     <li>{@link RedisClientConfig}
 *     <li>{@link LiteEnv}
 * </ul>
 */
@Module(includes = {RedisClientModule.class, BaseEnvModule.class})
public abstract class RedisAvsAccountStoreModule {

    @Binds
    abstract AvsVerifiedAccountStore bindAvsVerifiedAccountStore(RedisAvsVerifiedAccountStore impl);

    RedisAvsAccountStoreModule() {}
}
