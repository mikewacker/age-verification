package org.example.age.common.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpServerExchange;

/** Response sender that sends a JSON body, or an error status code. */
@FunctionalInterface
public interface JsonSender<B> extends Sender {

    static <B> JsonSender<B> create(HttpServerExchange exchange, ObjectMapper mapper) {
        return new JsonSenderImpl<>(exchange, mapper);
    }

    default void sendBody(B body) {
        send(HttpOptional.of(body));
    }

    @Override
    default void sendError(int statusCode) {
        send(HttpOptional.empty(statusCode));
    }

    void send(HttpOptional<B> maybeBody);
}
