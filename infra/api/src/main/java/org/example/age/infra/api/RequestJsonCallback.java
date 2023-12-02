package org.example.age.infra.api;

import io.undertow.server.HttpServerExchange;

/** Callback for a request whose JSON body has been read. */
@FunctionalInterface
public interface RequestJsonCallback<V> {

    void handleRequest(HttpServerExchange exchange, RequestParser parser, V value) throws Exception;
}
