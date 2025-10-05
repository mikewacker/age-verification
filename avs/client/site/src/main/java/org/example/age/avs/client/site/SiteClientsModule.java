package org.example.age.avs.client.site;

import com.google.common.collect.Maps;
import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import java.util.Map;
import org.example.age.common.client.api.ApiClientFactory;
import org.example.age.common.client.api.ApiClientModule;
import org.example.age.site.api.client.SiteApi;

/**
 * Dagger module that binds <code>Map&lt;String, {@link SiteApi}&gt;</code>.
 * <p>
 * Depends on an unbound...
 * <ul>
 *     <li>{@link SiteClientsConfig}
 *     <li>{@code LiteEnv}
 * </ul>
 */
@Module(includes = ApiClientModule.class)
public abstract class SiteClientsModule {

    @Provides
    @Singleton
    static Map<String, SiteApi> provideSiteClients(ApiClientFactory apiClientFactory, SiteClientsConfig config) {
        return Maps.transformValues(config.urls(), url -> apiClientFactory.create(url, SiteApi.class));
    }
}
