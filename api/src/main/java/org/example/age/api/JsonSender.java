package org.example.age.api;

/** Response sender that sends a JSON body, or an error status code. */
@FunctionalInterface
public interface JsonSender<B> extends Sender {

    default void sendBody(B body) {
        send(HttpOptional.of(body));
    }

    @Override
    default void sendError(int statusCode) {
        send(HttpOptional.empty(statusCode));
    }

    void send(HttpOptional<B> maybeBody);
}
