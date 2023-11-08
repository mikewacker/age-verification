package org.example.age.common.api;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;

/** Response sender that only sends a status code. */
@FunctionalInterface
public interface CodeSender extends Sender {

    static CodeSender create(HttpServerExchange exchange) {
        return new CodeSenderImpl(exchange);
    }

    default void sendOk() {
        send(StatusCodes.OK);
    }

    @Override
    default void sendError(int statusCode) {
        send(statusCode);
    }

    void send(int statusCode);
}
