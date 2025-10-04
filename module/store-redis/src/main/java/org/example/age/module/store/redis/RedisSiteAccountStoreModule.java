package org.example.age.module.store.redis;

import dagger.Binds;
import dagger.Module;
import org.example.age.common.env.BaseEnvModule;
import org.example.age.common.env.LiteEnv;
import org.example.age.common.provider.redis.RedisConfig;
import org.example.age.common.provider.redis.RedisModule;
import org.example.age.site.spi.SiteVerificationStore;

/**
 * Dagger module that binds {@link SiteVerificationStore}.
 * <p>
 * Depends on an unbound...
 * <ul>
 *     <li>{@link RedisConfig}
 *     <li>{@link LiteEnv}
 * </ul>
 */
@Module(includes = {RedisModule.class, BaseEnvModule.class})
public interface RedisSiteAccountStoreModule {

    @Binds
    SiteVerificationStore bindSiteVerificationStore(RedisSiteVerificationStore impl);
}
