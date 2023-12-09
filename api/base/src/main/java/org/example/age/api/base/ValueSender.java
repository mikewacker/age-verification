package org.example.age.api.base;

/** Response sender that sends a value, or an error status code. */
@FunctionalInterface
public interface ValueSender<V> extends Sender {

    default void sendValue(V value) {
        send(HttpOptional.of(value));
    }

    @Override
    default void sendErrorCode(int errorCode) {
        send(HttpOptional.empty(errorCode));
    }

    void send(HttpOptional<V> maybeValue);
}
