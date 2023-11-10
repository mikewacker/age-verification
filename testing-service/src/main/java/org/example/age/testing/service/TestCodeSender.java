package org.example.age.testing.service;

import java.util.concurrent.atomic.AtomicBoolean;
import org.example.age.api.CodeSender;

/** Fake {@link CodeSender} that stores the status code that was sent. */
public final class TestCodeSender implements CodeSender {

    private int statusCode;
    private AtomicBoolean wasSent = new AtomicBoolean(false);

    /** Creates a {@link TestCodeSender}. */
    public static TestCodeSender create() {
        return new TestCodeSender();
    }

    /** Determines if a response was sent. */
    public boolean wasSent() {
        return wasSent.get();
    }

    /** Gets the status code that was sent. */
    public int get() {
        if (!wasSent.get()) {
            throw new IllegalStateException("response has not been sent");
        }

        return statusCode;
    }

    @Override
    public void send(int statusCode) {
        if (wasSent.getAndSet(true)) {
            throw new IllegalStateException("response was already sent");
        }

        this.statusCode = statusCode;
    }

    private TestCodeSender() {}
}
