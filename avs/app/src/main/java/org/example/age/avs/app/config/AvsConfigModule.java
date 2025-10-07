package org.example.age.avs.app.config;

import dagger.Module;
import dagger.Provides;
import org.example.age.avs.client.site.SiteClientsConfig;
import org.example.age.avs.provider.userlocalizer.demo.AvsLocalizationKeysConfig;
import org.example.age.common.client.dynamodb.DynamoDbClientConfig;
import org.example.age.common.client.redis.RedisClientConfig;
import org.example.age.common.provider.signingkey.demo.EcPrivateKeyConfig;
import org.example.age.service.AvsServiceConfig;

/**
 * Dagger module that binds...
 * <ul>
 *     <li>{@link AvsServiceConfig}
 *     <li>{@link SiteClientsConfig}
 *     <li>{@link DynamoDbClientConfig}
 *     <li>{@link RedisClientConfig}
 *     <li>{@link EcPrivateKeyConfig}
 *     <li>{@link AvsLocalizationKeysConfig}
 * </ul>
 * <p>
 * Depends on an unbound {@link AvsAppConfig}.
 */
@Module
public interface AvsConfigModule {

    @Provides
    static AvsServiceConfig provideAvsServiceConfig(AvsAppConfig appConfig) {
        return appConfig.getService();
    }

    @Provides
    static SiteClientsConfig provideSiteClientsConfig(AvsAppConfig appConfig) {
        return appConfig.getClients().sites();
    }

    @Provides
    static DynamoDbClientConfig provideDynamoDbClientConfig(AvsAppConfig appConfig) {
        return appConfig.getClients().dynamoDb();
    }

    @Provides
    static RedisClientConfig provideRedisClientConfig(AvsAppConfig appConfig) {
        return appConfig.getClients().redis();
    }

    @Provides
    static EcPrivateKeyConfig provideEcPrivateKeyConfig(AvsAppConfig appConfig) {
        return appConfig.getKeys().signing();
    }

    @Provides
    static AvsLocalizationKeysConfig provideAvsLocalizationKeysConfig(AvsAppConfig appConfig) {
        return appConfig.getKeys().localization();
    }
}
