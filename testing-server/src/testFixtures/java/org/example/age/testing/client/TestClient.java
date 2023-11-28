package org.example.age.testing.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import java.io.IOException;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/** Shared HTTP client for testing. */
public final class TestClient {

    private static final Map<String, String> EMPTY_HEADERS = Map.of();
    private static final MediaType JSON_CONTENT_TYPE = MediaType.get("application/json");
    private static final RequestBody EMPTY_REQUEST_BODY = RequestBody.create(new byte[0]);

    private static final ObjectMapper mapper = createObjectMapper();

    /** Issues a simple, synchronous HTTP GET request, returning the response. */
    public static Response get(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        return execute(request);
    }

    /** Issues a simple, synchronous HTTP POST request, returning the response. */
    public static Response post(String url) throws IOException {
        return post(url, EMPTY_HEADERS, EMPTY_REQUEST_BODY);
    }

    /** Issues a synchronous HTTP POST request with headers, returning the response. */
    public static Response post(String url, Map<String, String> headers) throws IOException {
        return post(url, headers, EMPTY_REQUEST_BODY);
    }

    /** Issues a synchronous HTTP POST request with a JSON body, returning the response. */
    public static <B> Response post(String url, B body) throws IOException {
        RequestBody requestBody = createRequestBody(body);
        return post(url, EMPTY_HEADERS, requestBody);
    }

    /** Issues a synchronous HTTP POST request with headers and a JSON body, returning the response. */
    public static <B> Response post(String url, Map<String, String> headers, B body) throws IOException {
        RequestBody requestBody = createRequestBody(body);
        return post(url, headers, requestBody);
    }

    /** Issues a synchronous HTTP request, returning the response. */
    public static Response execute(Request request) throws IOException {
        return Holder.INSTANCE.newCall(request).execute();
    }

    /** Reads the response body. */
    public static <B> B readBody(Response response, TypeReference<B> bodyTypeRef) throws IOException {
        byte[] rawBody = response.body().bytes();
        return mapper.readValue(rawBody, bodyTypeRef);
    }

    /** Gets the shared client. */
    public static OkHttpClient getInstance() {
        return Holder.INSTANCE;
    }

    /** Creates a request body. */
    private static <B> RequestBody createRequestBody(B body) throws JsonProcessingException {
        byte[] rawBody = mapper.writeValueAsBytes(body);
        return RequestBody.create(rawBody, JSON_CONTENT_TYPE);
    }

    /** Issues a synchronous HTTP POST request, returning the response. */
    private static Response post(String url, Map<String, String> headers, RequestBody requestBody) throws IOException {
        Request.Builder requestBuilder = new Request.Builder().url(url);
        headers.forEach((name, value) -> requestBuilder.header(name, value));
        Request request = requestBuilder.post(requestBody).build();
        return execute(request);
    }

    /** Creates the {@link ObjectMapper}. */
    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new GuavaModule());
        return mapper;
    }

    /** Holder for the shared client. */
    private static final class Holder {

        public static OkHttpClient INSTANCE =
                new OkHttpClient.Builder().followRedirects(false).build();
    }

    // static class
    private TestClient() {}
}
