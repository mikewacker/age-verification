package org.example.age.testing;

import okhttp3.OkHttpClient;

/** Singleton HTTP client for testing. */
public final class TestClient {

    private static final OkHttpClient client = new OkHttpClient();

    /** Gets the HTTP client. */
    public static OkHttpClient get() {
        return client;
    }

    private TestClient() {} // static class
}
