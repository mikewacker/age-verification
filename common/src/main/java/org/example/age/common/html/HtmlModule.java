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
 * <p>It depends on an unbound {@code @Named("html") Class<?>},
 * a class in the same project as the {@code "resources"} folder that contains the static HTML files.
 * The root directory for the static HTML files will be {@code "resources/html"}.</p>
 */
@Module
public interface HtmlModule {

    @Provides
    @Named("html")
    @Singleton
    static HttpHandler provideHttpHandler(@Named("html") Class<?> clazz) {
        return HtmlHandler.create(clazz);
    }
}
