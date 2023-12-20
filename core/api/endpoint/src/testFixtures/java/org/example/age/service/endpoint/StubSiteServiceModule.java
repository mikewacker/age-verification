package org.example.age.service.endpoint;

import dagger.Binds;
import dagger.Module;
import io.undertow.server.HttpHandler;
import org.example.age.api.def.SiteApi;
import org.example.age.api.endpoint.SiteApiModule;
import org.example.age.api.extractor.AccountIdExtractor;
import org.example.age.api.extractor.AuthMatchDataExtractor;

/**
 * Dagger module that binds dependencies for <code>@Named("api") {@link HttpHandler}</code>.
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li>{@link AccountIdExtractor}</li>
 *     <li>{@link AuthMatchDataExtractor}</li>
 * </ul>
 */
@Module(includes = SiteApiModule.class)
public interface StubSiteServiceModule {

    @Binds
    SiteApi bindSiteApi(StubSiteService service);
}
