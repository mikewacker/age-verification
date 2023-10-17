package org.example.age.common.html;

import dagger.Module;
import dagger.Provides;
import io.undertow.server.HttpHandler;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Dagger module that publishes a binding for <code>@Named("html") {@link HttpHandler}</code>,
 * which serves static HTML files.
 *
 * <p>Depends on an unbound {@code @Named("html") Class<?>}, which is a class in the module with the static HTML files.
 * The root directory for the static HTML files is {@code "src/main/resources/html"}.</p>
 */
@Module
public interface HtmlModule {

    @Provides
    @Named("html")
    @Singleton
    static HttpHandler provideHtmlHttpHandler(@Named("html") Class<?> clazz) {
        return HtmlHandler.create(clazz);
    }
}
