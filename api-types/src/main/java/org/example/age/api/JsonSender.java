package org.example.age.api;

/** Response sender that sends a JSON body, or an error status code. */
@FunctionalInterface
public interface JsonSender<V> extends Sender {

    default void sendValue(V value) {
        send(HttpOptional.of(value));
    }

    @Override
    default void sendErrorCode(int errorCode) {
        send(HttpOptional.empty(errorCode));
    }

    void send(HttpOptional<V> maybeValue);
}
