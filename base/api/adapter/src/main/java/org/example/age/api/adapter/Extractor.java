package org.example.age.api.adapter;

import io.github.mikewacker.drift.api.HttpOptional;

/** Synchronously extracts a value (or an error status code) from the underlying request. */
@FunctionalInterface
public interface Extractor<E, V> {

    HttpOptional<V> tryExtract(E exchange);

    /** Adapts this extractor to the asynchronous interface. */
    default Async<E, V> async() {
        return (exchange, callback) -> {
            HttpOptional<V> maybeValue = tryExtract(exchange);
            callback.onValueExtracted(maybeValue);
        };
    }

    /**
     * Asynchronously extracts a value (or an error status code) from the underlying request.
     *
     * <p>An implementation may be synchronous or asynchronous.</p>
     */
    @FunctionalInterface
    interface Async<E, V> {

        void tryExtract(E exchange, Callback<V> callback) throws Exception;
    }

    /** Called when a value (or an error status code) has been extracted from the underlying request. */
    @FunctionalInterface
    interface Callback<V> {

        void onValueExtracted(HttpOptional<V> maybeValue) throws Exception;
    }
}
