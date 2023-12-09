package org.example.age.testing.client;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.example.age.api.base.HttpOptional;
import org.example.age.data.json.JsonValues;
import org.example.age.infra.client.JsonApiRequest;

/** Shared HTTP client for testing. */
public final class TestClient {

    private static final MediaType JSON_CONTENT_TYPE = MediaType.get("application/json");
    private static final MediaType HTML_CONTENT_TYPE = MediaType.get("text/html");

    private static final OkHttpClient client = createClient();

    /** Issues an HTTP GET request for HTML. */
    public static HttpOptional<String> getHtml(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            return HttpOptional.empty(response.code());
        }

        checkContentType(response, HTML_CONTENT_TYPE);
        String html = response.body().string();
        return HttpOptional.of(html);
    }

    /** Creates a builder for an HTTP request for a JSON API. */
    public static ApiRequestBuilder apiRequestBuilder() {
        return new ApiRequestBuilder();
    }

    /** Creates the shared {@link OkHttpClient}. */
    private static OkHttpClient createClient() {
        return new OkHttpClient.Builder().followRedirects(false).build();
    }

    /** Checks that the response has the expected content type. */
    private static void checkContentType(Response response, MediaType expectedContentType) {
        String rawContentType = response.header("Content-Type");
        if (rawContentType == null) {
            throw new RuntimeException("response Content-Type is missing");
        }

        MediaType contentType = MediaType.parse(rawContentType);
        if (contentType == null) {
            String message = String.format("failed to parse response Content-Type: %s", rawContentType);
            throw new RuntimeException(message);
        }

        if (!contentType.type().equals(expectedContentType.type())
                || !contentType.subtype().equals(expectedContentType.subtype())) {
            String message =
                    String.format("expected response Content-Type: %s (was: %s)", expectedContentType, contentType);
            throw new RuntimeException(message);
        }
    }

    // static class
    private TestClient() {}

    /** Builder for an HTTP request for a JSON API. */
    public static final class ApiRequestBuilder {

        private final JsonApiRequest.Builder requestBuilder = JsonApiRequest.builder(client);

        /** Uses a GET request at the specified URL. */
        public ApiRequestBuilder get(String url) {
            requestBuilder.get(url);
            return this;
        }

        /** Uses a POST request at the specified URL. */
        public ApiRequestBuilder post(String url) {
            requestBuilder.post(url);
            return this;
        }

        /** Sets the headers. */
        public ApiRequestBuilder headers(Map<String, String> headers) {
            requestBuilder.headers(headers);
            return this;
        }

        /** Sets the body. */
        public ApiRequestBuilder body(Object requestValue) {
            requestBuilder.body(requestValue);
            return this;
        }

        /** Makes the request synchronously, returning a status code. */
        public int executeWithStatusCodeResponse() throws IOException {
            Response response = requestBuilder.execute();
            return response.code();
        }

        /** Makes the request synchronously, returning a response value or an error status code. */
        public <V> HttpOptional<V> executeWithJsonResponse(TypeReference<V> responseValueTypeRef) throws IOException {
            Response response = requestBuilder.execute();
            if (!response.isSuccessful()) {
                return HttpOptional.empty(response.code());
            }

            checkContentType(response, JSON_CONTENT_TYPE);
            byte[] rawResponseValue = response.body().bytes();
            V responseValue = JsonValues.deserialize(rawResponseValue, responseValueTypeRef);
            return HttpOptional.of(responseValue);
        }

        private ApiRequestBuilder() {}
    }
}
