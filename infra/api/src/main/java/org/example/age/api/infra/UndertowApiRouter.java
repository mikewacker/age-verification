package org.example.age.api.infra;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import java.util.HashMap;
import java.util.Map;

/** Routes relative paths to {@link UndertowJsonApiHandler}'s (or other {@link HttpHandler}'s). */
public final class UndertowApiRouter implements HttpHandler {

    private static final HttpHandler defaultHandler = UndertowJsonApiHandler.notFound();

    private Map<String, HttpHandler> router;

    /** Creates a builder for an {@link UndertowApiRouter}. */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        HttpHandler handler = router.getOrDefault(exchange.getRelativePath(), defaultHandler);
        handler.handleRequest(exchange);
    }

    private UndertowApiRouter(Map<String, HttpHandler> router) {
        this.router = router;
    }

    /** Builder for an {@link UndertowApiRouter}. */
    public static final class Builder {

        private final Map<String, HttpHandler> router = new HashMap<>();

        /** Adds a route from a relative path to an {@link HttpHandler}. */
        public Builder addRoute(String relativePath, HttpHandler handler) {
            relativePath = relativePath.replaceFirst("^/?", "/");
            router.put(relativePath, handler);
            return this;
        }

        /** Builds an {@link UndertowApiRouter}. */
        public HttpHandler build() {
            return new UndertowApiRouter(router);
        }

        private Builder() {}
    }
}
