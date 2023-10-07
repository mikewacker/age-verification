package org.example.age.common.client;

import io.undertow.server.HttpServerExchange;
import java.util.concurrent.ExecutorService;
import javax.inject.Inject;
import javax.inject.Singleton;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;

@Singleton
final class ExchangeClientImpl implements ExchangeClient {

    private volatile OkHttpClient client = null;

    private final Object lock = new Object();

    @Inject
    public ExchangeClientImpl() {}

    @Override
    public OkHttpClient getInstance(HttpServerExchange exchange) {
        // Use double-checked locking.
        OkHttpClient localClient = client;
        if (localClient == null) {
            synchronized (lock) {
                localClient = client;
                if (localClient == null) {
                    localClient = createClient(exchange);
                    client = localClient;
                }
            }
        }
        return localClient;
    }

    /** Creates an HTTP client using the server's worker. */
    private static OkHttpClient createClient(HttpServerExchange exchange) {
        ExecutorService worker = exchange.getConnection().getWorker();
        Dispatcher dispatcher = new Dispatcher(worker);
        return new OkHttpClient.Builder().dispatcher(dispatcher).build();
    }
}
