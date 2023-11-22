package org.example.age.infra.api.request;

import io.undertow.server.HttpServerExchange;

/** Callback for a request whose body has been read. */
@FunctionalInterface
public interface RequestBodyCallback<B> {

    void handleRequest(HttpServerExchange exchange, RequestParser parser, B body) throws Exception;
}
