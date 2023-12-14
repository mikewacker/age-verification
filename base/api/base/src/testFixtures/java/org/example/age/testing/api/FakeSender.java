package org.example.age.testing.api;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.base.Sender;

/** Fake {@link Sender} that stores the response that was sent. */
public abstract class FakeSender<V> {

    private final AtomicReference<V> response = new AtomicReference<>(null);

    /** Gets the response if it was sent, or returns empty. */
    public final Optional<V> tryGet() {
        return Optional.ofNullable(response.get());
    }

    public final void send(V response) {
        if (!this.response.compareAndSet(null, response)) {
            throw new IllegalStateException("response was already sent");
        }
    }

    private FakeSender() {}

    /** Fake {@link Sender} that stores the status code that was sent. */
    public static final class StatusCode extends FakeSender<Integer> implements Sender.StatusCode {

        /** Creates a {@link FakeSender.StatusCode}. */
        public static FakeSender.StatusCode create() {
            return new FakeSender.StatusCode();
        }

        @Override
        public void send(int statusCode) {
            super.send(statusCode);
        }

        private StatusCode() {}
    }

    /** Fake {@link Sender} that stores the value (or error status code) that was sent. */
    public static final class Value<V> extends FakeSender<HttpOptional<V>> implements Sender.Value<V> {

        /** Creates a {@link FakeSender.Value}. */
        public static <V> FakeSender.Value<V> create() {
            return new FakeSender.Value<>();
        }

        private Value() {}
    }
}
