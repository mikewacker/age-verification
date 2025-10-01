package org.example.age.module.client;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.example.age.avs.api.client.AvsApi;
import org.example.age.common.env.EnvModule;
import org.example.age.common.env.LiteEnv;

/**
 * Dagger module that binds <code>@Named("client") {@link AvsApi}</code>.
 * <p>
 * Depends on an unbound...
 * <ul>
 *     <li>{@link SiteClientsConfig}
 *     <li>{@link LiteEnv}.
 * </ul>
 */
@Module(includes = EnvModule.class)
public interface SiteClientModule {

    @Provides
    @Named("client")
    @Singleton
    static AvsApi provideAvsClient(ApiClientFactory clientFactory, SiteClientsConfig clientsConfig) {
        return clientFactory.create(clientsConfig.avsUrl(), AvsApi.class);
    }
}
