package org.example.age.infra.client;

import java.io.IOException;
import java.util.Map;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * HTTP request for a JSON API.
 *
 * <p>Different consumers may have different requirements for...</p>
 * <ul>
 *     <li>how to create the {@link OkHttpClient}.</li>
 *     <li>how to process the {@link Response}.</li>
 *     <li>how to serialize and deserialize objects to and from JSON, including error handling.</li>
 *     <li>whether to support synchronous calls, asynchronous calls, or both.</li>
 * </ul>
 *
 * <p>In practice, consumers will create a specialized builder that is backed by a {@link JsonApiRequest.Builder}.</p>
 */
public final class JsonApiRequest {

    /** Creates a builder for an API request, using the specified client. */
    public static Builder builder(OkHttpClient client) {
        return new Builder(client);
    }

    // static class
    private JsonApiRequest() {}

    /** Builder for an API request. */
    public static final class Builder {

        private static final MediaType JSON_CONTENT_TYPE = MediaType.get("application/json");
        private static final RequestBody EMPTY_BODY = RequestBody.create(new byte[0]);

        private final OkHttpClient client;

        private String method = null;
        private String url = null;
        private Map<String, String> headers = Map.of();
        private RequestBody body = EMPTY_BODY;

        /** Uses a GET request at the specified URL. */
        public Builder get(String url) {
            method = "GET";
            this.url = url;
            return this;
        }

        /** Uses a POST request at the specified URL. */
        public Builder post(String url) {
            method = "POST";
            this.url = url;
            return this;
        }

        /** Sets the headers. */
        public Builder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        /** Sets the JSON body. */
        public Builder body(byte[] json) {
            body = RequestBody.create(json, JSON_CONTENT_TYPE);
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

        private Builder(OkHttpClient client) {
            this.client = client;
        }
    }
}
