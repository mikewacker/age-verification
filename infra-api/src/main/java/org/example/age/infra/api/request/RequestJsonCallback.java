package org.example.age.infra.api.request;

import io.undertow.server.HttpServerExchange;

/** Handles a request with a JSON body. */
@FunctionalInterface
public interface RequestJsonCallback<B> {

    void handleRequest(HttpServerExchange exchange, RequestParser parser, B body) throws Exception;
}
