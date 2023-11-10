package org.example.age.api;

/** Response sender that only sends a status code. */
@FunctionalInterface
public interface CodeSender extends Sender {

    default void sendOk() {
        send(200);
    }

    @Override
    default void sendError(int statusCode) {
        send(statusCode);
    }

    void send(int statusCode);
}
