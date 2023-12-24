package org.example.age.demo.server;

import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;

/** Factory that creates the root {@link HttpHandler}. */
final class RootHandlerFactory {

    /** Creates the root {@link HttpHandler} from the API {@link HttpHandler}. */
    public static HttpHandler create(HttpHandler apiHandler) {
        return new PathHandler().addPrefixPath("/api/", apiHandler);
    }

    // static class
    private RootHandlerFactory() {}
}
