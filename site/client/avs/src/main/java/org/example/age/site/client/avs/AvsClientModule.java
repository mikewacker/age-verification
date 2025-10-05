package org.example.age.site.client.avs;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import org.example.age.avs.api.client.AvsApi;
import org.example.age.common.client.api.ApiClientFactory;
import org.example.age.common.client.api.ApiClientModule;

/**
 * Dagger module that binds the {@link AvsApi} client.
 * <p>
 * Depends on an unbound...
 * <ul>
 *     <li>{@link AvsClientConfig}
 *     <li>{@code LiteEnv}
 * </ul>
 */
@Module(includes = ApiClientModule.class)
public abstract class AvsClientModule {

    @Provides
    @Singleton
    static AvsApi bindAvsClient(ApiClientFactory apiClientFactory, AvsClientConfig config) {
        return apiClientFactory.create(config.url(), AvsApi.class);
    }

    AvsClientModule() {}
}
