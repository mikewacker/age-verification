package org.example.age.testing.client;

import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import org.example.age.api.base.HttpOptional;

/** Shared HTTP client for HTML. */
public final class TestHtmlClient {

    private static final MediaType HTML_CONTENT_TYPE = MediaType.get("text/html");

    /** Gets HTML at the specified URL, or returns an error status code. */
    public static HttpOptional<String> get(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = TestOkHttpClient.get().newCall(request).execute();
        if (!response.isSuccessful()) {
            return HttpOptional.empty(response.code());
        }

        TestOkHttpClient.assertContentType(response, HTML_CONTENT_TYPE);
        String html = response.body().string();
        return HttpOptional.of(html);
    }

    // static class
    private TestHtmlClient() {}
}
