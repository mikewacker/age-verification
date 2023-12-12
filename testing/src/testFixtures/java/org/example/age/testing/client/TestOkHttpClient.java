package org.example.age.testing.client;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Shared {@link OkHttpClient} for testing.
 *
 * <p>Also asserts that the {@link Response} has the expected content type.</p>
 */
final class TestOkHttpClient {

    private static final OkHttpClient client = create();

    /** Gets the shared {@link OkHttpClient}. */
    public static OkHttpClient get() {
        return client;
    }

    /** Asserts that the {@link Response} has the expected content type. */
    public static void assertContentType(Response response, MediaType expectedContentType) {
        String rawContentType = response.header("Content-Type");
        if (rawContentType == null) {
            throw new AssertionError("response Content-Type is missing");
        }

        MediaType contentType = MediaType.parse(rawContentType);
        if (contentType == null) {
            String message = String.format("failed to parse response Content-Type: %s", rawContentType);
            throw new AssertionError(message);
        }

        if (!contentType.type().equals(expectedContentType.type())
                || !contentType.subtype().equals(expectedContentType.subtype())) {
            String message =
                    String.format("expected response Content-Type: %s (was: %s)", expectedContentType, contentType);
            throw new AssertionError(message);
        }
    }

    /** Creates the shared {@link OkHttpClient}. */
    private static OkHttpClient create() {
        return new OkHttpClient.Builder().followRedirects(false).build();
    }

    // static class
    private TestOkHttpClient() {}
}
