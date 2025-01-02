package org.example.age.service.util;

import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.WebApplicationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.IntUnaryOperator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Adapts an asynchronous Retrofit call to a completion stage. */
public final class AsyncCalls {

    /**
     * Makes an asynchronous {@link Call}, returning a {@link CompletionStage}
     * that completes with a successful response or a 500 error.
     */
    public static <T> CompletionStage<T> make(Call<T> call) {
        CompletableFuture<T> future = new CompletableFuture<>();
        call.enqueue(new FutureCallback<>(future, code -> 500));
        return future;
    }

    /**
     * Makes an asynchronous {@link Call}, returning a {@link CompletionStage}
     * that completes with a successful response or an error code based on the response's error code.
     */
    public static <T> CompletionStage<T> make(Call<T> call, IntUnaryOperator errorCodeMapper) {
        CompletableFuture<T> future = new CompletableFuture<>();
        call.enqueue(new FutureCallback<>(future, errorCodeMapper));
        return future;
    }

    // static class
    private AsyncCalls() {}

    /** Callback that completes a {@link CompletableFuture}. */
    private record FutureCallback<T>(CompletableFuture<T> future, IntUnaryOperator errorCodeMapper)
            implements Callback<T> {

        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            if (!response.isSuccessful()) {
                String message = String.format(
                        "%d error from %s", response.code(), call.request().url());
                int errorCode = errorCodeMapper.applyAsInt(response.code());
                future.completeExceptionally(new WebApplicationException(message, errorCode));
                return;
            }

            future.complete(response.body());
        }

        @Override
        public void onFailure(Call<T> call, Throwable throwable) {
            String message =
                    String.format("request to %s failed", call.request().url());
            future.completeExceptionally(new InternalServerErrorException(message, throwable));
        }
    }
}
