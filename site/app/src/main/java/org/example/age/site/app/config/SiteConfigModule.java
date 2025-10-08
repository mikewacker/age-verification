package org.example.age.site.app.config;

import dagger.Module;
import dagger.Provides;
import org.example.age.common.client.dynamodb.DynamoDbClientConfig;
import org.example.age.common.client.redis.RedisClientConfig;
import org.example.age.common.provider.signingkey.demo.EcPublicKeyConfig;
import org.example.age.site.client.avs.AvsClientConfig;
import org.example.age.site.endpoint.SiteEndpointConfig;
import org.example.age.site.provider.userlocalizer.demo.SiteLocalizationKeyConfig;

/**
 * Dagger module that binds...
 * <ul>
 *     <li>{@link SiteEndpointConfig}
 *     <li>{@link AvsClientConfig}
 *     <li>{@link DynamoDbClientConfig}
 *     <li>{@link RedisClientConfig}
 *     <li>{@link EcPublicKeyConfig}
 *     <li>{@link SiteLocalizationKeyConfig}
 * </ul>
 * <p>
 * Depends on an unbound {@link SiteAppConfig}.
 */
@Module
public abstract class SiteConfigModule {

    @Provides
    static SiteEndpointConfig provideSiteEndpointConfig(SiteAppConfig appConfig) {
        return appConfig.getEndpoint();
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
    static EcPublicKeyConfig provideEcPublicKeyConfig(SiteAppConfig appConfig) {
        return appConfig.getKeys().signing();
    }

    @Provides
    static SiteLocalizationKeyConfig provideSiteLocalizationKeyConfig(SiteAppConfig appConfig) {
        return appConfig.getKeys().localization();
    }

    SiteConfigModule() {}
}
