package org.example.age.module.client;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import org.example.age.api.client.retrofit.ApiClient;

/** Creates clients for services. */
@Singleton
final class ServiceClientFactory {

    private final ApiClient apiClient;

    @Inject
    public ServiceClientFactory(@Named("worker") ExecutorService worker) {
        this.apiClient = createApiClient(worker);
    }

    /** Creates a client for the service. */
    public <S> S create(URL baseUrl, Class<S> serviceClass) {
        apiClient.getAdapterBuilder().baseUrl(baseUrl);
        return apiClient.createService(serviceClass);
    }

    /** Creates the API client from the worker. */
    private static ApiClient createApiClient(ExecutorService worker) {
        Dispatcher dispatcher = new Dispatcher(worker);
        OkHttpClient httpClient =
                new OkHttpClient.Builder().dispatcher(dispatcher).build();
        return new ApiClient(httpClient);
    }
}
