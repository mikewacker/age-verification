package org.example.age.demo.server;

import dagger.Module;
import dagger.Provides;
import io.undertow.server.HttpHandler;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Dagger module that publishes a binding for {@link HttpHandler}.
 *
 * <p>Depends on an unbound <code>@Named("api") {@link HttpHandler}</code>.</p>
 */
@Module
interface RootModule {

    @Provides
    @Singleton
    static HttpHandler provideRootHandler(@Named("api") HttpHandler apiHandler) {
        return RootHandlerFactory.create(apiHandler);
    }
}
