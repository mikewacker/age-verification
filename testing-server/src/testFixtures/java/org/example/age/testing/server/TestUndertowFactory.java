package org.example.age.testing.server;

import io.undertow.Undertow;

/** Factory that creates an {@link Undertow} server that listens on the specified port. */
@FunctionalInterface
public interface TestUndertowFactory {

    Undertow create(int port);
}
