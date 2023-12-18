package org.example.age.api.endpoint.site;

import dagger.Module;
import dagger.Provides;
import io.undertow.server.HttpHandler;
import javax.inject.Named;
import javax.inject.Singleton;
import org.example.age.api.def.site.SiteApi;
import org.example.age.api.extractor.common.AccountIdExtractor;
import org.example.age.api.extractor.common.AuthMatchDataExtractor;

/**
 * Dagger module that publishes a binding for <code>@Named("api") {@link HttpHandler}</code>.
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li>{@link SiteApi}</li>
 *     <li>{@link AccountIdExtractor}</li>
 *     <li>{@link AuthMatchDataExtractor}</li>
 * </ul>
 */
@Module
public interface SiteApiModule {

    @Provides
    @Named("api")
    @Singleton
    static HttpHandler provideApiHandler(
            SiteApi api, AccountIdExtractor accountIdExtractor, AuthMatchDataExtractor authDataExtractor) {
        return SiteApiEndpoint.createHandler(api, accountIdExtractor, authDataExtractor);
    }
}
