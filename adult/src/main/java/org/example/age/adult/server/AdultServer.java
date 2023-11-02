package org.example.age.adult.server;

import com.google.common.net.HostAndPort;
import dagger.BindsInstance;
import dagger.Component;
import io.undertow.Undertow;
import javax.inject.Singleton;

/** Factory that creates the {@link Undertow} server. */
public final class AdultServer {

    /** Creates the {@link Undertow} server. */
    public static Undertow create(HostAndPort hostAndPort) {
        return ServerComponent.createServer(hostAndPort);
    }

    /** Dagger component that provides an {@link Undertow} server. */
    @Component(modules = AdultUndertowModule.class)
    @Singleton
    interface ServerComponent {

        static Undertow createServer(HostAndPort hostAndPort) {
            ServerComponent component =
                    DaggerAdultServer_ServerComponent.factory().create(hostAndPort);
            return component.server();
        }

        Undertow server();

        @Component.Factory
        interface Factory {

            ServerComponent create(@BindsInstance HostAndPort hostAndPort);
        }
    }

    // static class
    private AdultServer() {}
}
