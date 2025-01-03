package org.example.age.api.endpoint;

import dagger.Module;
import dagger.Provides;
import io.undertow.server.HttpHandler;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.example.age.api.def.AvsApi;
import org.example.age.api.extractor.AccountIdExtractor;
import org.example.age.api.extractor.AuthMatchDataExtractor;

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
