package org.example.age.infra.api.request;

import io.undertow.server.HttpServerExchange;

/** Callback for a request whose JSON body has been read. */
@FunctionalInterface
public interface RequestJsonCallback<B> {

    void handleRequest(HttpServerExchange exchange, RequestParser parser, B body) throws Exception;
}
