package org.example.age.testing.api;

import java.util.Optional;
import org.example.age.api.HttpOptional;
import org.example.age.api.JsonSender;

/** Fake {@link JsonSender} that stores the body (or error status code) that was sent. */
public final class FakeJsonSender<B> implements JsonSender<B> {

    private Optional<HttpOptional<B>> maybeResponse = Optional.empty();

    /** Creates a {@link FakeJsonSender}. */
    public static <B> FakeJsonSender<B> create() {
        return new FakeJsonSender<>();
    }

    /** Gets the body (or error status code) that was sent, if a response was sent. */
    public Optional<HttpOptional<B>> tryGet() {
        return maybeResponse;
    }

    @Override
    public void send(HttpOptional<B> maybeBody) {
        if (maybeResponse.isPresent()) {
            throw new IllegalStateException("response was already sent");
        }

        maybeResponse = Optional.of(maybeBody);
    }

    private FakeJsonSender() {}
}
