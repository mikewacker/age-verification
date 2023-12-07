package org.example.age.testing.api;

import java.util.Optional;
import org.example.age.api.HttpOptional;
import org.example.age.api.JsonSender;

/** Fake {@link JsonSender} that stores the body (or error status code) that was sent. */
public final class FakeJsonSender<V> implements JsonSender<V> {

    private Optional<HttpOptional<V>> maybeResponse = Optional.empty();

    /** Creates a {@link FakeJsonSender}. */
    public static <V> FakeJsonSender<V> create() {
        return new FakeJsonSender<>();
    }

    /** Gets the body (or error status code) that was sent, if a response was sent. */
    public Optional<HttpOptional<V>> tryGet() {
        return maybeResponse;
    }

    @Override
    public void send(HttpOptional<V> maybeValue) {
        if (maybeResponse.isPresent()) {
            throw new IllegalStateException("response was already sent");
        }

        maybeResponse = Optional.of(maybeValue);
    }

    private FakeJsonSender() {}
}
