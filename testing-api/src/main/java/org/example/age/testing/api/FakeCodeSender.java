package org.example.age.testing.api;

import java.util.OptionalInt;
import org.example.age.api.CodeSender;

/** Fake {@link CodeSender} that stores the status code that was sent. */
public final class FakeCodeSender implements CodeSender {

    private OptionalInt maybeStatusCode = OptionalInt.empty();

    /** Creates a {@link FakeCodeSender}. */
    public static FakeCodeSender create() {
        return new FakeCodeSender();
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

    private FakeCodeSender() {}
}
