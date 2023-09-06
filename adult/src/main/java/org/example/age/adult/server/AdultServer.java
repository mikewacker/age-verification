package org.example.age.adult.server;

import dagger.Component;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import javax.inject.Named;
import javax.inject.Singleton;
import org.example.age.adult.html.VerifyHtmlModule;

/** Factory for the Undertow server. */
public final class AdultServer {

    /** Creates a server. */
    public static Undertow create(String host, int port) {
        HttpHandler handler = ServerComponent.createRootHandler();
        return Undertow.builder()
                .addHttpListener(port, host)
                .setHandler(handler)
                .build();
    }

    /** Dagger component that provides the root {@link HttpHandler}. */
    @Component(modules = VerifyHtmlModule.class)
    @Singleton
    interface ServerComponent {

        static HttpHandler createRootHandler() {
            ServerComponent component = DaggerAdultServer_ServerComponent.create();
            return component.rootHandler();
        }

        @Named("verifyHtml")
        HttpHandler rootHandler();
    }

    // static class
    private AdultServer() {}
}
