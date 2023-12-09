package org.example.age.api.base;

/** Response sender that only sends a status code. */
@FunctionalInterface
public interface StatusCodeSender extends Sender {

    default void sendOk() {
        send(200);
    }

    @Override
    default void sendErrorCode(int errorCode) {
        send(errorCode);
    }

    void send(int statusCode);
}
