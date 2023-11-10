package org.example.age.infra.service.client.internal;

import javax.inject.Inject;
import javax.inject.Singleton;
import okhttp3.OkHttpClient;
import org.example.age.api.Dispatcher;

@Singleton
final class ExchangeClientImpl implements ExchangeClient {

    private volatile OkHttpClient client = null;

    private final Object lock = new Object();

    @Inject
    public ExchangeClientImpl() {}

    @Override
    public OkHttpClient getInstance(Dispatcher dispatcher) {
        // Use double-checked locking.
        OkHttpClient localClient = client;
        if (localClient == null) {
            synchronized (lock) {
                localClient = client;
                if (localClient == null) {
                    localClient = createClient(dispatcher);
                    client = localClient;
                }
            }
        }
        return localClient;
    }

    /** Creates an HTTP client using the server's worker. */
    private static OkHttpClient createClient(Dispatcher dispatcher) {
        okhttp3.Dispatcher clientDispatcher = new okhttp3.Dispatcher(dispatcher.getWorker());
        return new OkHttpClient.Builder().dispatcher(clientDispatcher).build();
    }
}
