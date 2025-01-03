package org.example.age.testing;

import okhttp3.OkHttpClient;

/** {@link OkHttpClient} singleton for testing. */
public final class TestClient {

    private static final OkHttpClient client = new OkHttpClient();

    /** Gets the {@link OkHttpClient}. */
    public static OkHttpClient get() {
        return client;
    }

    // static class
    private TestClient() {}
}
