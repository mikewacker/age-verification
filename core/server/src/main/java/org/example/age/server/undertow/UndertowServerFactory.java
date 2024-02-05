package org.example.age.server.undertow;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;

/** Factory that creates an Undertow server. */
final class UndertowServerFactory {

    /** Creates an Undertow server from the host, the port, and the root HTTP handler. */
    public static Undertow create(String host, int port, HttpHandler rootHandler) {
        return Undertow.builder()
                .addHttpListener(port, host)
                .setHandler(rootHandler)
                .build();
    }

    // static class
    private UndertowServerFactory() {}
}
