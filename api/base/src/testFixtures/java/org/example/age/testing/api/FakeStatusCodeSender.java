package org.example.age.testing.api;

import java.util.OptionalInt;
import org.example.age.api.StatusCodeSender;

/** Fake {@link StatusCodeSender} that stores the status code that was sent. */
public final class FakeStatusCodeSender implements StatusCodeSender {

    private OptionalInt maybeStatusCode = OptionalInt.empty();

    /** Creates a {@link FakeStatusCodeSender}. */
    public static FakeStatusCodeSender create() {
        return new FakeStatusCodeSender();
    }

    /** Gets the status code that was sent, if a response was sent. */
    public OptionalInt tryGet() {
        return maybeStatusCode;
    }

    @Override
    public void send(int statusCode) {
        if (maybeStatusCode.isPresent()) {
            throw new IllegalStateException("response was already sent");
        }

        maybeStatusCode = OptionalInt.of(statusCode);
    }

    private FakeStatusCodeSender() {}
}
