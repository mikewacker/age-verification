package org.example.age.demo.server;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;

/** Factory that creates an {@link Undertow} server. */
final class UndertowServerFactory {

    /** Creates an {@link Undertow} server from the host, the port, and the root {@link HttpHandler}. */
    public static Undertow create(String host, int port, HttpHandler rootHandler) {
        return Undertow.builder()
                .addHttpListener(port, host)
                .setHandler(rootHandler)
                .build();
    }

    // static class
    private UndertowServerFactory() {}
}
