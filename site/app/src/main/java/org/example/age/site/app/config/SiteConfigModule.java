package org.example.age.site.app.config;

import dagger.Module;
import dagger.Provides;
import org.example.age.common.client.dynamodb.DynamoDbClientConfig;
import org.example.age.common.client.redis.RedisClientConfig;
import org.example.age.module.crypto.demo.keys.SiteKeysConfig;
import org.example.age.service.SiteServiceConfig;
import org.example.age.site.client.avs.AvsClientConfig;

/**
 * Dagger module that binds...
 * <ul>
 *     <li>{@link SiteServiceConfig}
 *     <li>{@link AvsClientConfig}
 *     <li>{@link DynamoDbClientConfig}
 *     <li>{@link RedisClientConfig}
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
    static AvsClientConfig provieAvsClientConfig(SiteAppConfig appConfig) {
        return appConfig.getClients().avs();
    }

    @Provides
    static DynamoDbClientConfig provideDynamoDbClientConfig(SiteAppConfig appConfig) {
        return appConfig.getClients().dynamoDb();
    }

    @Provides
    static RedisClientConfig provideRedisClientConfig(SiteAppConfig appConfig) {
        return appConfig.getClients().redis();
    }

    @Provides
    static SiteKeysConfig provideSiteKeysConfig(SiteAppConfig appConfig) {
        return appConfig.getKeys();
    }
}
