package org.example.age.module.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.util.concurrent.ExecutorService;
import org.example.age.api.client.AvsApi;

/**
 * Dagger module that binds <code>@Named("client") {@link AvsApi}</code>.
 * <p>
 * Depends on an unbound...
 * <ul>
 *     <li>{@link SiteClientsConfig}
 *     <li>{@link ObjectMapper}
 *     <li><code>@Named("worker") {@link ExecutorService}</code>
 * </ul>
 */
@Module
public interface SiteClientModule {

    @Provides
    @Named("client")
    @Singleton
    static AvsApi provideAvsClient(ApiClientFactory clientFactory, SiteClientsConfig clientsConfig) {
        return clientFactory.create(clientsConfig.avsUrl(), AvsApi.class);
    }
}
