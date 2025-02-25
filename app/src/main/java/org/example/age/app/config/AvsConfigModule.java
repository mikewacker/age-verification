package org.example.age.app.config;

import dagger.Module;
import dagger.Provides;
import org.example.age.module.client.AvsClientsConfig;
import org.example.age.module.crypto.demo.AvsKeysConfig;
import org.example.age.module.store.redis.RedisConfig;
import org.example.age.service.AvsServiceConfig;

/**
 * Dagger module that binds...
 * <ul>
 *     <li>{@link AvsServiceConfig}
 *     <li>{@link AvsClientsConfig}
 *     <li>{@link RedisConfig}
 *     <li>{@link AvsKeysConfig}
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
    static AvsClientsConfig provieAvsClientsConfig(AvsAppConfig appConfig) {
        return appConfig.getClients();
    }

    @Provides
    static RedisConfig provideRedisConfig(AvsAppConfig appConfig) {
        return appConfig.getRedis();
    }

    @Provides
    static AvsKeysConfig provideAvsKeysConfig(AvsAppConfig appConfig) {
        return appConfig.getKeys();
    }
}
