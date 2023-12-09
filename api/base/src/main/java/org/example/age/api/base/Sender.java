package org.example.age.api.base;

/**
 * Response sender that sends an error status code.
 *
 * <p>Sub-interfaces will specify how to send to a successful response.</p>
 */
@FunctionalInterface
public interface Sender {

    void sendErrorCode(int errorCode);
}
