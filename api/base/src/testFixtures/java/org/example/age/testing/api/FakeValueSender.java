package org.example.age.testing.api;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.base.ValueSender;

/** Fake {@link ValueSender} that stores the value (or error status code) that was sent. */
public final class FakeValueSender<V> implements ValueSender<V> {

    private final AtomicReference<HttpOptional<V>> maybeValue = new AtomicReference<>(null);

    /** Creates a {@link FakeValueSender}. */
    public static <V> FakeValueSender<V> create() {
        return new FakeValueSender<>();
    }

    /** Gets the value (or error status code) that was sent, if a response was sent. */
    public Optional<HttpOptional<V>> tryGet() {
        return Optional.ofNullable(maybeValue.get());
    }

    @Override
    public void send(HttpOptional<V> maybeValue) {
        if (!this.maybeValue.compareAndSet(null, maybeValue)) {
            throw new IllegalStateException("response was already sent");
        }
    }

    private FakeValueSender() {}
}
