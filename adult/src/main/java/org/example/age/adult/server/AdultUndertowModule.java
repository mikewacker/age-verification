package org.example.age.adult.server;

import com.google.common.net.HostAndPort;
import dagger.Module;
import dagger.Provides;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import javax.inject.Named;
import javax.inject.Singleton;
import org.example.age.adult.html.VerifyHtmlModule;
import org.example.age.common.server.UndertowModule;

/**
 * Dagger module that binds dependencies needed to create an {@link Undertow}.
 *
 * <p>Depends on an unbound <code>Supplier&lt;{@link HostAndPort}&gt;</code>.</p>
 */
@Module(includes = {UndertowModule.class, VerifyHtmlModule.class})
interface AdultUndertowModule {

    @Provides
    @Named("api")
    @Singleton
    static HttpHandler provideApiHandler() {
        return exchange -> {
            exchange.setStatusCode(404);
            exchange.endExchange();
        };
    }
}
