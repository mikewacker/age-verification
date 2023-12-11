package org.example.age.service.avs.endpoint;

import dagger.Binds;
import dagger.Module;
import io.undertow.server.HttpHandler;
import org.example.age.api.avs.AvsApi;
import org.example.age.api.avs.endpoint.AvsApiModule;
import org.example.age.module.extractor.common.AccountIdExtractor;
import org.example.age.module.extractor.common.AuthMatchDataExtractor;

/**
 * Dagger module that binds dependencies for <code>@Named("api") {@link HttpHandler}</code>.
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li>{@link AccountIdExtractor}</li>
 *     <li>{@link AuthMatchDataExtractor}</li>
 * </ul>
 */
@Module(includes = AvsApiModule.class)
public interface StubAvsServiceModule {

    @Binds
    AvsApi bindAvsApi(StubAvsService service);
}
