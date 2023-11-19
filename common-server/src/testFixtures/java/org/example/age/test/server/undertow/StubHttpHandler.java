package org.example.age.test.server.undertow;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Stub {@link HttpHandler} that always responds with a 404 error. */
@Singleton
final class StubHttpHandler implements HttpHandler {

    @Inject
    public StubHttpHandler() {}

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        exchange.setStatusCode(StatusCodes.NOT_FOUND);
        exchange.endExchange();
    }
}
