package org.example.age.common.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpServerExchange;

/** Response sender that sends a JSON body, or an error status code. */
@FunctionalInterface
public interface JsonSender<T> extends Sender {

    static <T> JsonSender<T> create(HttpServerExchange exchange, ObjectMapper mapper) {
        return new JsonSenderImpl<>(exchange, mapper);
    }

    default void sendBody(T body) {
        send(HttpOptional.of(body));
    }

    default void sendError(int statusCode) {
        send(HttpOptional.empty(statusCode));
    }

    void send(HttpOptional<T> maybeBody);
}
