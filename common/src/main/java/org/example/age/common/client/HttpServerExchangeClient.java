package org.example.age.common.client;

import io.undertow.server.HttpServerExchange;
import okhttp3.OkHttpClient;

/**
 * Shared HTTP client for exchanges on the server. The client uses the worker of the Undertow server for async calls.
 *
 * <p>The client is lazily initialized on the first exchange;
 * we may not have access to the worker until after the server starts.</p>
 */
@FunctionalInterface
public interface HttpServerExchangeClient {

    OkHttpClient getInstance(HttpServerExchange exchange);
}
