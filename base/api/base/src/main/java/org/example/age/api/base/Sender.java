package org.example.age.api.base;

/**
 * Response sender that sends an error status code.
 *
 * <p>Sub-interfaces will specify how to send to a successful response.</p>
 */
@FunctionalInterface
public interface Sender {

    void sendErrorCode(int errorCode);

    /** Response sender that only sends a status code. */
    @FunctionalInterface
    interface StatusCode extends Sender {

        default void sendOk() {
            send(200);
        }

        @Override
        default void sendErrorCode(int errorCode) {
            send(errorCode);
        }

        void send(int statusCode);
    }

    /** Response sender that sends a value (or an error status code). */
    @FunctionalInterface
    interface Value<V> extends Sender {

        default void sendValue(V value) {
            send(HttpOptional.of(value));
        }

        @Override
        default void sendErrorCode(int errorCode) {
            send(HttpOptional.empty(errorCode));
        }

        default <U> void sendErrorCode(HttpOptional<U> emptyValue) {
            sendErrorCode(emptyValue.statusCode());
        }

        void send(HttpOptional<V> maybeValue);
    }
}
