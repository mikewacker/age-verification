package org.example.age.service.endpoint.avs;

import dagger.Binds;
import dagger.Module;
import io.undertow.server.HttpHandler;
import org.example.age.api.def.avs.AvsApi;
import org.example.age.api.endpoint.avs.AvsApiModule;
import org.example.age.api.module.extractor.common.AccountIdExtractor;
import org.example.age.api.module.extractor.common.AuthMatchDataExtractor;

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
