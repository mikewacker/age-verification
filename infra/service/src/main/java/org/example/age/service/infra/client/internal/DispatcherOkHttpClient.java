package org.example.age.service.infra.client.internal;

import okhttp3.OkHttpClient;
import org.example.age.api.base.Dispatcher;

/**
 * Shared {@link OkHttpClient} for backend requests. The client uses the worker of the underlying server
 * (i.e., {@link Dispatcher#getWorker()}).
 *
 * <p>The client is lazily initialized on the first request.</p>
 */
@FunctionalInterface
public interface DispatcherOkHttpClient {

    OkHttpClient get(Dispatcher dispatcher);
}
