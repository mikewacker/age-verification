package org.example.age.service.infra.client.internal;

import javax.inject.Inject;
import javax.inject.Singleton;
import okhttp3.OkHttpClient;
import org.example.age.api.base.Dispatcher;

@Singleton
final class DispatcherOkHttpClientImpl implements DispatcherOkHttpClient {

    private volatile OkHttpClient client = null;

    private final Object lock = new Object();

    @Inject
    public DispatcherOkHttpClientImpl() {}

    @Override
    public OkHttpClient get(Dispatcher dispatcher) {
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

    /** Creates an {@link OkHttpClient} using the worker of the underlying server. */
    private static OkHttpClient createClient(Dispatcher dispatcher) {
        okhttp3.Dispatcher clientDispatcher = new okhttp3.Dispatcher(dispatcher.getWorker());
        return new OkHttpClient.Builder().dispatcher(clientDispatcher).build();
    }
}
