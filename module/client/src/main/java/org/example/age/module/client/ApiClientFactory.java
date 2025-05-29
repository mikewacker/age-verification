package org.example.age.module.client;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import org.example.age.module.common.LiteEnv;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/** Factory for API clients. */
@Singleton
final class ApiClientFactory {

    private final OkHttpClient httpClient;
    private final Converter.Factory jsonConverterFactory;

    @Inject
    public ApiClientFactory(LiteEnv env) {
        httpClient = createApiClient(env.worker());
        jsonConverterFactory = JacksonConverterFactory.create(env.jsonMapper());
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
