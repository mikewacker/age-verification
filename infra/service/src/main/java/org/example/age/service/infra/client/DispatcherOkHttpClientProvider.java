package org.example.age.service.infra.client;

import io.github.mikewacker.drift.api.Dispatcher;
import okhttp3.OkHttpClient;

/**
 * Provider for a shared {@link OkHttpClient} that is used for backend requests.
 * The client uses the worker of the underlying server (i.e., {@link Dispatcher#getWorker()}).
 *
 * <p>The client is lazily initialized on the first call to {@link #get(Dispatcher)}.</p>
 */
final class DispatcherOkHttpClientProvider {

    private volatile OkHttpClient client = null;
    private final Object lock = new Object();

    /** Creates a {@link DispatcherOkHttpClientProvider}. */
    public static DispatcherOkHttpClientProvider create() {
        return new DispatcherOkHttpClientProvider();
    }

    /** Gets the shared {@link OkHttpClient}. */
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

    private DispatcherOkHttpClientProvider() {}
}
