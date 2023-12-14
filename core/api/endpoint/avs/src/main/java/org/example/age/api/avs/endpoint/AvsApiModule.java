package org.example.age.api.avs.endpoint;

import dagger.Binds;
import dagger.Module;
import io.undertow.server.HttpHandler;
import javax.inject.Named;
import org.example.age.api.avs.AvsApi;
import org.example.age.module.extractor.common.AccountIdExtractor;
import org.example.age.module.extractor.common.AuthMatchDataExtractor;

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

    @Binds
    @Named("api")
    HttpHandler bindApiHandler(AvsApiEndpointHandler impl);
}
