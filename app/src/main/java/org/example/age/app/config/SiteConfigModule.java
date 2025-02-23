package org.example.age.app.config;

import dagger.Module;
import dagger.Provides;
import org.example.age.module.client.SiteClientsConfig;
import org.example.age.module.crypto.demo.SiteKeysConfig;
import org.example.age.module.store.redis.RedisConfig;
import org.example.age.service.SiteServiceConfig;

/**
 * Dagger module that binds...
 * <ul>
 *     <li>{@link SiteServiceConfig}
 *     <li>{@link SiteClientsConfig}
 *     <li>{@link RedisConfig}
 *     <li>{@link SiteKeysConfig}
 * </ul>
 * <p>
 * Depends on an unbound {@link SiteAppConfig}.
 */
@Module
public interface SiteConfigModule {

    @Provides
    static SiteServiceConfig provideSiteServiceConfig(SiteAppConfig appConfig) {
        return appConfig.getService();
    }

    @Provides
    static SiteClientsConfig provieSiteClientsConfig(SiteAppConfig appConfig) {
        return appConfig.getClients();
    }

    @Provides
    static RedisConfig provideRedisConfig(SiteAppConfig appConfig) {
        return appConfig.getRedis();
    }

    @Provides
    static SiteKeysConfig provideSiteKeysConfig(SiteAppConfig appConfig) {
        return appConfig.getKeys();
    }
}
