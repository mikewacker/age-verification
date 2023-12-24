package org.example.age.demo.server;

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
 *     <li>{@code @Named("host") String}</li>
 *     <li>{@code @Named("port") int}</li>
 *     <li>{@link HttpHandler}</li>
 * </ul>
 */
@Module
interface UndertowModule {

    @Provides
    @Singleton
    static Undertow create(@Named("host") String host, @Named("port") int port, HttpHandler handler) {
        return UndertowServerFactory.create(host, port, handler);
    }
}
