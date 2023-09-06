package org.example.age.adult.html;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/** Overlays an age verification check on top of static HTML files. */
@Singleton
final class VerificationHandler implements HttpHandler {

    private static final String FAVICON_PATH = "/favicon.ico";

    private final HttpHandler next;
    private final String verifyPath;

    private final Set<String> uncheckedPaths;

    @Inject
    public VerificationHandler(@Named("html") HttpHandler next, @Named("verifyPath") String verifyPath) {
        this.next = next;
        this.verifyPath = verifyPath;
        uncheckedPaths = Set.of(FAVICON_PATH, verifyPath);
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        if (uncheckedPaths.contains(exchange.getRequestPath())) {
            next.handleRequest(exchange);
            return;
        }

        unverifiedRedirect(exchange);
    }

    /** Redirects to the verification page. */
    private void unverifiedRedirect(HttpServerExchange exchange) {
        exchange.setStatusCode(StatusCodes.SEE_OTHER);
        exchange.getResponseHeaders().put(Headers.LOCATION, verifyPath);
    }
}
