package org.example.age.common.server.root;

import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;

/** Root {@link HttpHandler}. */
final class RootHandler {

    /** Creates a root {@link HttpHandler} from handlers for the API and the HTML site. */
    public static HttpHandler create(HttpHandler apiHandler, HttpHandler htmlHandler) {
        PathHandler handler = new PathHandler(htmlHandler);
        handler.addPrefixPath("/api/", apiHandler);
        return handler;
    }

    // static class
    private RootHandler() {}
}
