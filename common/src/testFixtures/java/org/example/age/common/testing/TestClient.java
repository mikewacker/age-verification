package org.example.age.common.testing;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.function.Consumer;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/** HTTP and API clients for testing. All clients are backed by a singleton HTTP client. */
public final class TestClient {

    private static final OkHttpClient client = new OkHttpClient();
    private static final Converter.Factory jsonConverterFactory =
            JacksonConverterFactory.create(TestObjectMapper.get());

    /** Gets the HTTP client. */
    public static OkHttpClient http() {
        return client;
    }

    /** Creates an API client. */
    public static <A> A api(int port, Consumer<Request.Builder> requestInterceptor, Class<A> apiType) {
        return new Retrofit.Builder()
                .baseUrl(localhostUrl(port))
                .client(http(requestInterceptor))
                .addConverterFactory(jsonConverterFactory)
                .build()
                .create(apiType);
    }

    /** Creates a URL for localhost. */
    public static URL localhostUrl(int port) {
        try {
            return localhostUri(port).toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /** Creates a URI for localhost. */
    public static URI localhostUri(int port) {
        return URI.create(String.format("http://localhost:%d", port));
    }

    /** Creates an HTTP client that intercepts the request. */
    private static OkHttpClient http(Consumer<Request.Builder> requestInterceptor) {
        return http().newBuilder()
                .addInterceptor(chain -> interceptRequest(chain, requestInterceptor))
                .build();
    }

    /** Interceptor that modifies the request. */
    private static Response interceptRequest(Interceptor.Chain chain, Consumer<Request.Builder> requestInterceptor)
            throws IOException {
        Request.Builder requestBuilder = chain.request().newBuilder();
        requestInterceptor.accept(requestBuilder);
        Request request = requestBuilder.build();
        return chain.proceed(request);
    }

    private TestClient() {} // static class
}
