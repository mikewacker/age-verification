package org.example.age.demo.server;

import dagger.Module;
import dagger.Provides;
import io.undertow.Undertow;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.example.age.module.location.resource.ResourceAvsLocationModule;
import org.example.age.server.undertow.UndertowModule;
import org.example.age.service.location.RefreshableAvsLocationProvider;

/**
 * Dagger module that binds dependencies for {@link Undertow}.
 *
 * <p>Depends on an unbound {@code @Named("name") String}.</p>
 */
@Module(
        includes = {
            UndertowModule.class,
            ResourceAvsLocationModule.class,
            DemoAvsServiceModule.class,
        })
interface DemoAvsServerModule {

    @Provides
    @Named("host")
    @Singleton
    static String provideHost(RefreshableAvsLocationProvider avsLocationProvider) {
        return avsLocationProvider.getAvs().host();
    }

    @Provides
    @Named("port")
    @Singleton
    static int providePort(RefreshableAvsLocationProvider avsLocationProvider) {
        return avsLocationProvider.getAvs().port();
    }
}
