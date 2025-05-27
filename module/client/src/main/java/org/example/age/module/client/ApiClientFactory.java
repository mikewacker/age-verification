package org.example.age.module.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/** Factory for API clients. */
@Singleton
final class ApiClientFactory {

    private final OkHttpClient httpClient;
    private final Converter.Factory jsonConverterFactory;

    @Inject
    public ApiClientFactory(ObjectMapper mapper, @Named("worker") ExecutorService worker) {
        httpClient = createApiClient(worker);
        jsonConverterFactory = JacksonConverterFactory.create(mapper);
    }

    /** Creates an API client. */
    public <A> A create(URL baseUrl, Class<A> apiType) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(httpClient)
                .addConverterFactory(jsonConverterFactory)
                .build()
                .create(apiType);
    }

    /** Creates the HTTP client from the worker. */
    private static OkHttpClient createApiClient(ExecutorService worker) {
        return new OkHttpClient.Builder().dispatcher(new Dispatcher(worker)).build();
    }
}
