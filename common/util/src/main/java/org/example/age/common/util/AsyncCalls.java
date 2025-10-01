package org.example.age.common.util;

import jakarta.ws.rs.WebApplicationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Adapts an asynchronous Retrofit call to a completion stage. */
public final class AsyncCalls {

    /**  Makes an asynchronous call, returning a corresponding stage. */
    public static <T> CompletionStage<T> make(Call<T> call) {
        CompletableFuture<T> future = new CompletableFuture<>();
        call.enqueue(new FutureCallback<>(future));
        return future;
    }

    private AsyncCalls() {} // static class

    /** Callback that completes a future. */
    private record FutureCallback<T>(CompletableFuture<T> future) implements Callback<T> {

        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            if (!response.isSuccessful()) {
                String message = String.format(
                        "%d error from %s", response.code(), call.request().url());
                future.completeExceptionally(new WebApplicationException(message, response.code()));
                return;
            }

            future.complete(response.body());
        }

        @Override
        public void onFailure(Call<T> call, Throwable throwable) {
            future.completeExceptionally(throwable);
        }
    }
}
