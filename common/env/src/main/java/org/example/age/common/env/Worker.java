package org.example.age.common.env;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/** Worker thread pool. */
public interface Worker {

    /** Dispatches a task to a worker thread. */
    <V> CompletableFuture<V> dispatch(Supplier<V> task);
}
