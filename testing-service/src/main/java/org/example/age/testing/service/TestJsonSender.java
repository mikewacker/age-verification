package org.example.age.testing.service;

import java.util.concurrent.atomic.AtomicBoolean;
import org.example.age.common.api.HttpOptional;
import org.example.age.common.api.JsonSender;

/** Fake {@link JsonSender} that stores the body (or error status code) that was sent. */
public final class TestJsonSender<B> implements JsonSender<B> {

    private HttpOptional<B> maybeBody;
    private AtomicBoolean wasSent = new AtomicBoolean(false);

    /** Creates a {@link TestJsonSender}. */
    public static TestJsonSender create() {
        return new TestJsonSender();
    }

    /** Determines if a response was sent. */
    public boolean wasSent() {
        return wasSent.get();
    }

    /** Gets the body (or error status code) that was sent. */
    public HttpOptional<B> get() {
        if (!wasSent.get()) {
            throw new IllegalStateException("response has not been sent");
        }

        return maybeBody;
    }

    @Override
    public void send(HttpOptional maybeBody) {
        if (wasSent.getAndSet(true)) {
            throw new IllegalStateException("response was already sent");
        }

        this.maybeBody = maybeBody;
    }

    private TestJsonSender() {}
}
