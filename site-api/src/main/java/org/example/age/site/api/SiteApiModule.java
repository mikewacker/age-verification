package org.example.age.site.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Binds;
import dagger.Module;
import io.undertow.server.HttpHandler;
import javax.inject.Named;
import org.example.age.common.api.data.AccountIdExtractor;
import org.example.age.common.api.data.AuthMatchDataExtractor;
import org.example.age.infra.api.data.JsonSerializerModule;

/**
 * Dagger module that publishes a binding for <code>@Named("api") {@link HttpHandler}</code>.
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li>{@link SiteApi}</li>
 *     <li>{@link AccountIdExtractor}</li>
 *     <li>{@link AuthMatchDataExtractor}</li>
 *     <li>{@link ObjectMapper}</li>
 * </ul>
 */
@Module(includes = JsonSerializerModule.class)
public interface SiteApiModule {

    @Binds
    @Named("api")
    HttpHandler bindApiHttpHandler(SiteEndpointHandler impl);
}
