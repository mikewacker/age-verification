package org.example.age.common.api.exchange.impl;

import io.undertow.server.HttpServerExchange;

/** Handles a request with a deserialized body. */
@FunctionalInterface
public interface RequestBodyCallback<T> {

    void handleRequest(HttpServerExchange exchange, T requestBody) throws Exception;
}
