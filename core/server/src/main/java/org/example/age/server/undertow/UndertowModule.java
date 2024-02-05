package org.example.age.server.undertow;

import dagger.BindsOptionalOf;
import dagger.Module;
import dagger.Provides;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import java.util.Optional;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Dagger module that publishes a binding for {@link Undertow}.
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li>{@code @Named("host") String}</li>
 *     <li>{@code @Named("port") int}</li>
 *     <li><code>@Named("api") {@link HttpHandler}</code></li>
 *     <li>(optional) {@code @Named("html") Class<?>}</li>
 *     <li>(optional) <code>@Named("ajax") {@link HttpHandler}</code></li>
 * </ul>
 *
 * <p>The API handler handles any requests whose path starts with {@code /api/}.
 * The AJAX handler, if present, handles any requests whose path starts with {@code /ajax/}.
 * All other requests are routed to the HTML handler, if present, or result in a 404 error.</p>
 *
 * <p>The root directory for the HTML files is {@code resources/html};
 * the {@code @Named("html") Class<?>} is any class from the module with the resources.</p>
 */
@Module
public interface UndertowModule {

    @Provides
    @Singleton
    static Undertow provideUndertowServer(
            @Named("host") String host, @Named("port") int port, HttpHandler rootHandler) {
        return UndertowServerFactory.create(host, port, rootHandler);
    }

    @Provides
    @Singleton
    static HttpHandler provideRootHandler(
            @Named("api") HttpHandler apiHandler,
            @Named("html") Optional<HttpHandler> maybeHtmlHandler,
            @Named("ajax") Optional<HttpHandler> maybeAjaxHandler) {
        return RootHandlerFactory.create(apiHandler, maybeHtmlHandler, maybeAjaxHandler);
    }

    @Provides
    @Singleton
    @Named("html")
    static Optional<HttpHandler> provideOptionalHtmlHandler(@Named("html") Optional<Class<?>> maybeHtmlClass) {
        return maybeHtmlClass.map(HtmlHandlerFactory::create);
    }

    @BindsOptionalOf
    @Named("html")
    Class<?> bindOptionalHtmlClass();

    @BindsOptionalOf
    @Named("ajax")
    HttpHandler bindOptionalAjaxHandler();
}
