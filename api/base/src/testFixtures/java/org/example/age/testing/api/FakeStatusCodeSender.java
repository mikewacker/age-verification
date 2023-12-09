package org.example.age.testing.api;

import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicReference;
import org.example.age.api.base.StatusCodeSender;

/** Fake {@link StatusCodeSender} that stores the status code that was sent. */
public final class FakeStatusCodeSender implements StatusCodeSender {

    private final AtomicReference<Integer> statusCode = new AtomicReference<>(null);

    /** Creates a {@link FakeStatusCodeSender}. */
    public static FakeStatusCodeSender create() {
        return new FakeStatusCodeSender();
    }

    /** Gets the status code that was sent, if a response was sent. */
    public OptionalInt tryGet() {
        Integer statusCode = this.statusCode.get();
        return (statusCode != null) ? OptionalInt.of(statusCode) : OptionalInt.empty();
    }

    @Override
    public void send(int statusCode) {
        if (!this.statusCode.compareAndSet(null, statusCode)) {
            throw new IllegalStateException("response was already sent");
        }
    }

    private FakeStatusCodeSender() {}
}
