package org.example.age.common.server;

import com.google.common.net.HostAndPort;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;

/** Factory that crates the {@link Undertow} server. */
final class UndertowFactory {

    /** Creates the {@link Undertow} server. */
    public static Undertow create(HttpHandler handler, HostAndPort hostAndPort) {
        return Undertow.builder()
                .addHttpListener(hostAndPort.getPort(), hostAndPort.getHost())
                .setHandler(handler)
                .build();
    }

    // static class
    private UndertowFactory() {}
}
