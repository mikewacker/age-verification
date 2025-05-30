package org.example.age.app.config;

import dagger.Module;
import dagger.Provides;
import org.example.age.module.client.SiteClientsConfig;
import org.example.age.module.crypto.demo.keys.SiteKeysConfig;
import org.example.age.module.store.dynamodb.client.DynamoDbConfig;
import org.example.age.module.store.redis.client.RedisConfig;
import org.example.age.service.SiteServiceConfig;

/**
 * Dagger module that binds...
 * <ul>
 *     <li>{@link SiteServiceConfig}
 *     <li>{@link SiteClientsConfig}
 *     <li>{@link RedisConfig}
 *     <li>{@link DynamoDbConfig}
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
    static DynamoDbConfig provideDynamoDbConfig(SiteAppConfig appConfig) {
        return appConfig.getDynamoDb();
    }

    @Provides
    static SiteKeysConfig provideSiteKeysConfig(SiteAppConfig appConfig) {
        return appConfig.getKeys();
    }
}
