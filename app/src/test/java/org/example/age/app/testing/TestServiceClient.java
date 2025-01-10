package org.example.age.app.testing;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.example.age.api.client.retrofit.ApiClient;
import org.example.age.testing.TestClient;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/** Service client for testing. */
public final class TestServiceClient<S> implements BeforeAllCallback {

    private final int port;
    private final String accountId;
    private final Class<S> serviceClass;
    private S client;

    /** Creates a client that uses the provided account ID. */
    public TestServiceClient(int port, String accountId, Class<S> serviceClass) {
        this.port = port;
        this.accountId = accountId;
        this.serviceClass = serviceClass;
    }

    /** Gets the client. */
    public S get() {
        return client;
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        client = create();
    }

    /** Creates the client. */
    private S create() {
        OkHttpClient httpClient = TestClient.get()
                .newBuilder()
                .addInterceptor(chain -> chain.proceed(addAccountId(chain.request())))
                .build();
        ApiClient apiClient = new ApiClient(httpClient);
        String baseUrl = String.format("http://localhost:%d", port);
        apiClient.getAdapterBuilder().baseUrl(baseUrl);
        return apiClient.createService(serviceClass);
    }

    /** Adds the account ID to the request. */
    private Request addAccountId(Request request) {
        return request.newBuilder().header("Account-Id", accountId).build();
    }
}
