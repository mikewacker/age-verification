package org.example.age.api.endpoint.avs;

import dagger.Module;
import dagger.Provides;
import io.undertow.server.HttpHandler;
import javax.inject.Named;
import javax.inject.Singleton;
import org.example.age.api.def.avs.AvsApi;
import org.example.age.api.module.extractor.common.AccountIdExtractor;
import org.example.age.api.module.extractor.common.AuthMatchDataExtractor;

/**
 * Dagger module that publishes a binding for <code>@Named("api") {@link HttpHandler}</code>.
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li>{@link AvsApi}</li>
 *     <li>{@link AccountIdExtractor}</li>
 *     <li>{@link AuthMatchDataExtractor}</li>
 * </ul>
 */
@Module
public interface AvsApiModule {

    @Provides
    @Named("api")
    @Singleton
    static HttpHandler provideApiHandler(
            AvsApi api, AccountIdExtractor accountIdExtractor, AuthMatchDataExtractor authDataExtractor) {
        return AvsApiEndpoint.createHandler(api, accountIdExtractor, authDataExtractor);
    }
}
