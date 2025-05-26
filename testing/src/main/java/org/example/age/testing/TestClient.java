package org.example.age.testing;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import okhttp3.OkHttpClient;

/** Singleton HTTP client for testing. */
public final class TestClient {

    private static final OkHttpClient client = new OkHttpClient();

    /** Gets the HTTP client. */
    public static OkHttpClient get() {
        return client;
    }

    /** Creates a URL for localhost. */
    public static URL createLocalhostUrl(int port) {
        try {
            return createLocalhostUri(port).toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /** Creates a URI for localhost. */
    public static URI createLocalhostUri(int port) {
        return URI.create(String.format("http://localhost:%d", port));
    }

    private TestClient() {} // static class
}
