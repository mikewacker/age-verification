package org.example.age.common.client.api;

import jakarta.inject.Inject;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import jakarta.inject.Singleton;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import org.example.age.common.env.LiteEnv;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/** Implementation of {@link ApiClientFactory}. */
@Singleton
final class ApiClientFactoryImpl implements ApiClientFactory {

    private final OkHttpClient httpClient;
    private final Converter.Factory jsonConverterFactory;

    @Inject
    public ApiClientFactoryImpl(LiteEnv env) {
        httpClient = createHttpClient(env.worker());
        jsonConverterFactory = JacksonConverterFactory.create(env.jsonMapper());
    }

    @Override
    public <A> A create(URL baseUrl, Class<A> apiType) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(httpClient)
                .addConverterFactory(jsonConverterFactory)
                .build()
                .create(apiType);
    }

    /** Creates the HTTP client from the worker. */
    private static OkHttpClient createHttpClient(ExecutorService worker) {
        return new OkHttpClient.Builder().dispatcher(new Dispatcher(worker)).build();
    }
}
