package org.example.age.server.infra.root;

import dagger.Module;
import dagger.Provides;
import io.undertow.server.HttpHandler;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Dagger module that publishes a binding for {@link HttpHandler}.
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li><code>@Named("api") {@link HttpHandler}</code></li>
 *     <li><code>@Named("dynamicHtml") {@link HttpHandler}</code>: overlays dynamic logic on top of static HTML</li>
 * </ul>
 */
@Module
public interface RootModule {

    @Provides
    @Singleton
    static HttpHandler provideRootHandler(
            @Named("api") HttpHandler apiHandler, @Named("dynamicHtml") HttpHandler htmlHandler) {
        return RootHandler.create(apiHandler, htmlHandler);
    }
}
