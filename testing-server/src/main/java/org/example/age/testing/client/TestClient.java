package org.example.age.testing.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.example.age.data.DataMapper;

/** Shared HTTP client for testing. */
public final class TestClient {

    private static final MediaType JSON_CONTENT_TYPE = MediaType.get("application/json");
    private static final RequestBody EMPTY_REQUEST_BODY = RequestBody.create(new byte[0]);

    private static final ObjectMapper mapper = DataMapper.get();

    /** Issues a simple, synchronous HTTP GET request, returning the response. */
    public static Response get(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        return execute(request);
    }

    /** Issues a simple, synchronous HTTP POST request, returning the response. */
    public static Response post(String url) throws IOException {
        Request request =
                new Request.Builder().url(url).post(EMPTY_REQUEST_BODY).build();
        return execute(request);
    }

    /** Issues a synchronous HTTP POST request with a JSON body, returning the response. */
    public static <B> Response post(String url, B body) throws IOException {
        byte[] rawBody = mapper.writeValueAsBytes(body);
        RequestBody requestBody = RequestBody.create(rawBody, JSON_CONTENT_TYPE);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        return execute(request);
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

    /** Holder for the shared client. */
    private static final class Holder {

        public static OkHttpClient INSTANCE =
                new OkHttpClient.Builder().followRedirects(false).build();
    }

    // static class
    private TestClient() {}
}
