package org.example.age.service.infra.client;

import okhttp3.OkHttpClient;
import org.example.age.api.base.Dispatcher;

/**
 * Shared {@link OkHttpClient} for backend requests. The client uses the worker of the underlying server
 * (i.e., {@link Dispatcher#getWorker()}).
 *
 * <p>The client is lazily initialized on the first call to {@link #get(Dispatcher)}.</p>
 */
final class DispatcherOkHttpClient {

    private volatile OkHttpClient client = null;
    private final Object lock = new Object();

    /** Creates a {@link DispatcherOkHttpClient}. */
    public static DispatcherOkHttpClient create() {
        return new DispatcherOkHttpClient();
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

    private DispatcherOkHttpClient() {}
}
