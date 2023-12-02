package org.example.age.testing.server;

import io.undertow.server.HttpHandler;

/** Factory that creates an {@link HttpHandler}. */
@FunctionalInterface
public interface TestHandlerFactory {

    HttpHandler create();
}
