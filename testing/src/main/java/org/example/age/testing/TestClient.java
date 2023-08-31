package org.example.age.testing;

import okhttp3.OkHttpClient;

/** Singleton HTTP client for testing. */
public class TestClient {

    /** Gets the singleton client. */
    public static OkHttpClient getInstance() {
        return Holder.INSTANCE;
    }

    /** Holder for the singleton instance. */
    private static final class Holder {

        public static OkHttpClient INSTANCE = new OkHttpClient();
    }

    // static class
    private TestClient() {}
}
