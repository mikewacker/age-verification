package org.example.age.client.infra;

import java.io.IOException;
import java.util.Map;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.example.age.data.json.JsonValues;

/**
 * HTTP client for a JSON API.
 *
 * <p>Different consumers may have different requirements for...</p>
 * <ul>
 *     <li>how to create the {@link OkHttpClient}.</li>
 *     <li>how to process the {@link Response}.</li>
 *     <li>whether to support synchronous calls, asynchronous calls, or both.</li>
 * </ul>
 *
 * <p>In practice, consumers will create a specialized builder that is backed by a {@link RequestBuilder}.
 * This specialized builder will typically use the {@code OkHttp} library in the implementation,
 * but not expose it as part of the public API.</p>
 */
public final class JsonApiClient {

    /** Creates a builder for a JSON API request, using the specified client. */
    public static RequestBuilder requestBuilder(OkHttpClient client) {
        return new RequestBuilder(client);
    }

    // static class
    private JsonApiClient() {}

    /** Builder for a JSON API request. */
    public static final class RequestBuilder {

        private static final MediaType JSON_CONTENT_TYPE = MediaType.get("application/json");
        private static final RequestBody EMPTY_BODY = RequestBody.create(new byte[0]);

        private final OkHttpClient client;

        private String method = null;
        private String url = null;
        private Map<String, String> headers = Map.of();
        private RequestBody body = EMPTY_BODY;

        /** Uses a GET request at the specified URL. */
        public RequestBuilder get(String url) {
            method = "GET";
            this.url = url;
            return this;
        }

        /** Uses a POST request at the specified URL. */
        public RequestBuilder post(String url) {
            method = "POST";
            this.url = url;
            return this;
        }

        /** Sets the headers. */
        public RequestBuilder headers(Map<String, String> headers) {
            this.headers = Map.copyOf(headers);
            return this;
        }

        /** Sets the body. */
        public RequestBuilder body(Object requestValue) {
            byte[] rawRequestValue = JsonValues.serialize(requestValue);
            body = RequestBody.create(rawRequestValue, JSON_CONTENT_TYPE);
            return this;
        }

        /** Makes the request synchronously, returning the response. */
        public Response execute() throws IOException {
            Request request = build();
            return client.newCall(request).execute();
        }

        /** Makes the request asynchronously, using a callback. */
        public void enqueue(Callback callback) {
            Request request = build();
            client.newCall(request).enqueue(callback);
        }

        /** Builds the request. */
        private Request build() {
            checkMethodAndUrlSet();
            checkBodyNotSetForGetRequest();
            Request.Builder requestBuilder = new Request.Builder().url(url);
            headers.forEach((name, value) -> requestBuilder.header(name, value));
            if (method.equals("GET")) {
                requestBuilder.get();
            } else {
                requestBuilder.method(method, body);
            }
            return requestBuilder.build();
        }

        /** Checks that the method and URL have been set. */
        private void checkMethodAndUrlSet() {
            if (method == null) {
                throw new IllegalStateException("method and URL are not set");
            }
        }

        /** Checks that a GET request does not have a body. */
        private void checkBodyNotSetForGetRequest() {
            if (!method.equals("GET")) {
                return;
            }

            if (body != EMPTY_BODY) {
                throw new IllegalStateException("body is set for GET request");
            }
        }

        private RequestBuilder(OkHttpClient client) {
            this.client = client;
        }
    }
}
