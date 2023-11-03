package org.example.age.common.server.undertow;

import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;

/** Factory that creates the root {@link HttpHandler}. */
final class RootHandlerFactory {

    /** Creates the root {@link HttpHandler} from handlers for the API and the HTML site. */
    public static HttpHandler create(HttpHandler apiHandler, HttpHandler htmlHandler) {
        PathHandler handler = new PathHandler(htmlHandler);
        handler.addPrefixPath("/api/", apiHandler);
        return handler;
    }

    // static class
    private RootHandlerFactory() {}
}
