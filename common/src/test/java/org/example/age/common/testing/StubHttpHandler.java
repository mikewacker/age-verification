package org.example.age.common.testing;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.common.api.exchange.impl.ExchangeUtils;

/** Stub {@link HttpHandler} that always responds with a 404 error. */
@Singleton
public final class StubHttpHandler implements HttpHandler {

    @Inject
    public StubHttpHandler() {}

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        ExchangeUtils.sendStatusCode(exchange, StatusCodes.NOT_FOUND);
    }
}
