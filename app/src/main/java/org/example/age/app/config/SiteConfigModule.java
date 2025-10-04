package org.example.age.app.config;

import dagger.Module;
import dagger.Provides;
import org.example.age.common.client.dynamodb.DynamoDbClientConfig;
import org.example.age.common.client.redis.RedisClientConfig;
import org.example.age.module.client.SiteClientsConfig;
import org.example.age.module.crypto.demo.keys.SiteKeysConfig;
import org.example.age.service.SiteServiceConfig;

/**
 * Dagger module that binds...
 * <ul>
 *     <li>{@link SiteServiceConfig}
 *     <li>{@link SiteClientsConfig}
 *     <li>{@link RedisClientConfig}
 *     <li>{@link DynamoDbClientConfig}
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
    static RedisClientConfig provideRedisConfig(SiteAppConfig appConfig) {
        return appConfig.getRedis();
    }

    @Provides
    static DynamoDbClientConfig provideDynamoDbConfig(SiteAppConfig appConfig) {
        return appConfig.getDynamoDb();
    }

    @Provides
    static SiteKeysConfig provideSiteKeysConfig(SiteAppConfig appConfig) {
        return appConfig.getKeys();
    }
}
