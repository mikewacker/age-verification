package org.example.age.common.env;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

/** Implementation of {@link Worker}. */
@Singleton
final class WorkerImpl implements Worker {

    private final ExecutorService worker;

    @Inject
    public WorkerImpl(LiteEnv env) {
        worker = env.worker();
    }

    @Override
    public <V> CompletableFuture<V> dispatch(Supplier<V> task) {
        return CompletableFuture.supplyAsync(task, worker);
    }
}
