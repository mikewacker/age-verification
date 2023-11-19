package org.example.age.test.common.server.undertow;

import com.google.common.net.HostAndPort;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import javax.inject.Named;
import javax.inject.Singleton;
import org.example.age.common.server.undertow.UndertowModule;

/**
 * Dagger module that binds dependencies needed to create an {@link Undertow}.
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li><code>@Named("api") {@link HttpHandler}</code></li>
 *     <li><code>@Named("port") int</code></li>
 * </ul>
 */
@Module(includes = UndertowModule.class)
public interface TestUndertowModule {

    @Binds
    @Named("verifyHtml")
    @Singleton
    HttpHandler bindVerifyHtmlHttpHandler(StubHttpHandler impl);

    @Provides
    @Singleton
    static HostAndPort provideHostAndPort(@Named("port") int port) {
        return HostAndPort.fromParts("localhost", port);
    }
}
