package org.example.age.common.server.undertow;

import com.google.common.net.HostAndPort;
import dagger.Module;
import dagger.Provides;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Dagger module that publishes a binding for {@link Undertow}.
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li><code>@Named("api") {@link HttpHandler}</code></li>
 *     <li><code>@Named("verifyHtml") {@link HttpHandler}</code>:
 *         overlays an age verification check on top of static HTML files</li>
 *     <li>{@link HostAndPort}</li>
 * </ul>
 */
@Module
public interface UndertowModule {

    @Provides
    @Singleton
    static Undertow provideUndertow(HttpHandler handler, HostAndPort hostAndPort) {
        return UndertowFactory.create(handler, hostAndPort);
    }

    @Provides
    @Singleton
    static HttpHandler provideHandler(
            @Named("api") HttpHandler apiHandler, @Named("verifyHtml") HttpHandler htmlHandler) {
        return RootHandlerFactory.create(apiHandler, htmlHandler);
    }
}
