package org.example.age.infra.service.client.internal;

import okhttp3.OkHttpClient;
import org.example.age.api.Dispatcher;

/**
 * Shared HTTP client for exchanges on the server. The client uses the server's worker for async calls.
 *
 * <p>The client is lazily initialized on the first exchange;
 * we may not have access to the worker until after the server starts.</p>
 */
@FunctionalInterface
public interface ExchangeClient {

    OkHttpClient getInstance(Dispatcher dispatcher);
}
