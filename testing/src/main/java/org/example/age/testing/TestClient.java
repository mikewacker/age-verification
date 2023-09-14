package org.example.age.testing;

import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/** Shared HTTP client for testing. */
public final class TestClient {

    /** Issues a simple, synchronous HTTP GET request using the shared client, returning the response. */
    public static Response get(String url) throws IOException {
        OkHttpClient client = getInstance();
        Request request = new Request.Builder().url(url).build();
        return client.newCall(request).execute();
    }

    /** Gets the shared client. */
    public static OkHttpClient getInstance() {
        return Holder.INSTANCE;
    }

    /** Holder for the shared instance. */
    private static final class Holder {

        public static OkHttpClient INSTANCE =
                new OkHttpClient.Builder().followRedirects(false).build();
    }

    // static class
    private TestClient() {}
}
