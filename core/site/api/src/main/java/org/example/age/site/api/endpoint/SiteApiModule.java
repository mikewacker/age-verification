package org.example.age.site.api.endpoint;

import dagger.Binds;
import dagger.Module;
import io.undertow.server.HttpHandler;
import javax.inject.Named;
import org.example.age.common.api.extractor.AccountIdExtractor;
import org.example.age.common.api.extractor.AuthMatchDataExtractor;
import org.example.age.site.api.data.internal.SiteApiJsonSerializerModule;

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
@Module(includes = SiteApiJsonSerializerModule.class)
public interface SiteApiModule {

    @Binds
    @Named("api")
    HttpHandler bindApiHandler(SiteEndpointHandler impl);
}
