package org.example.age.server.undertow;

import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import java.util.Optional;

/** Factory that create a root HTTP handler. */
final class RootHandlerFactory {

    /** Creates a root HTTP handler from an API handler and optional HTML and AJAX handlers. */
    public static HttpHandler create(
            HttpHandler apiHandler, Optional<HttpHandler> maybeHtmlHandler, Optional<HttpHandler> maybeAjaxHandler) {
        PathHandler rootHandler =
                maybeHtmlHandler.isPresent() ? new PathHandler(maybeHtmlHandler.get()) : new PathHandler();
        rootHandler.addPrefixPath("/api/", apiHandler);
        maybeAjaxHandler.ifPresent(ajaxHandler -> rootHandler.addPrefixPath("/ajax/", ajaxHandler));
        return rootHandler;
    }

    // static class
    private RootHandlerFactory() {}
}
