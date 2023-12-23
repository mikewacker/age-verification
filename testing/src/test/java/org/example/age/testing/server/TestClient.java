package org.example.age.testing.server;

import okhttp3.OkHttpClient;

/** Shared {@link OkHttpClient} for testing. */
public class TestClient {

    private static final OkHttpClient client = new OkHttpClient();

    /** Gets the shared {@link OkHttpClient}. */
    public static OkHttpClient get() {
        return client;
    }

    // static class
    private TestClient() {}
}
