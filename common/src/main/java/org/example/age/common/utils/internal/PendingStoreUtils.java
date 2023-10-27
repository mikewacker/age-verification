package org.example.age.common.utils.internal;

import io.undertow.server.HttpServerExchange;
import org.example.age.common.store.internal.PendingStore;
import org.example.age.data.certificate.VerificationSession;
import org.xnio.XnioExecutor;

/** Utilities for {@link PendingStore}. */
public final class PendingStoreUtils {

    /** Inserts a key-value pair that is associated with a {@link VerificationSession}. */
    public static <K, V> void putForVerificationSession(
            PendingStore<K, V> store, K key, V value, VerificationSession session, HttpServerExchange exchange) {
        long expiration = session.verificationRequest().expiration();
        XnioExecutor executor = exchange.getIoThread();
        store.put(key, value, expiration, executor);
    }

    // static class
    private PendingStoreUtils() {}
}
