package org.example.age.module.store.redis;

import dagger.Binds;
import dagger.Module;
import org.example.age.common.client.redis.RedisClientConfig;
import org.example.age.common.client.redis.RedisClientModule;
import org.example.age.common.env.BaseEnvModule;
import org.example.age.common.env.LiteEnv;
import org.example.age.site.spi.SiteVerificationStore;

/**
 * Dagger module that binds {@link SiteVerificationStore}.
 * <p>
 * Depends on an unbound...
 * <ul>
 *     <li>{@link RedisClientConfig}
 *     <li>{@link LiteEnv}
 * </ul>
 */
@Module(includes = {RedisClientModule.class, BaseEnvModule.class})
public interface RedisSiteAccountStoreModule {

    @Binds
    SiteVerificationStore bindSiteVerificationStore(RedisSiteVerificationStore impl);
}
