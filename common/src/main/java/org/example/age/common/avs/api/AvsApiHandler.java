package org.example.age.common.avs.api;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * HTTP handler for the age verification service's API.
 *
 * <p>In a real implementation, calls to a site would use HTTPS.</p>
 */
@Singleton
final class AvsApiHandler implements HttpHandler {

    @Inject
    public AvsApiHandler() {}

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        // TODO: Implement.
        exchange.setStatusCode(StatusCodes.NOT_FOUND);
        exchange.endExchange();
    }
}
