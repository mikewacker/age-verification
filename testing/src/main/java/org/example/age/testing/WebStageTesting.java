package org.example.age.testing;

import static org.assertj.core.api.Assertions.fail;

import jakarta.ws.rs.WebApplicationException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.mock.Calls;

/** Test utilities for an asynchronous stage that is returned by a JAX-RS API. */
public final class WebStageTesting {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(5);

    /** Waits for the asynchronous stage to complete and returns its value, asserting that it completed successfully. */
    public static <V> V await(CompletionStage<V> stage) {
        return await(stage, DEFAULT_TIMEOUT);
    }

    /** Waits for the asynchronous stage to complete and returns its value, asserting that it completed successfully. */
    public static <V> V await(CompletionStage<V> stage, Duration timeout) {
        try {
            return get(stage, timeout);
        } catch (ExecutionException execE) {
            Throwable cause = execE.getCause();
            int errorCode = getErrorCode(cause);
            String message = String.format("%d error", errorCode);
            fail(message, cause);
        } catch (TimeoutException | InterruptedException e) {
            fail(e);
        }
        return null;
    }

    /** Waits for the asynchronous stage to complete, asserting that it completed with an HTTP error code. */
    public static void awaitErrorCode(CompletionStage<?> stage, int expectedErrorCode) {
        awaitErrorCode(stage, expectedErrorCode, DEFAULT_TIMEOUT);
    }

    /** Waits for the asynchronous stage to complete, asserting that it completed with an HTTP error code. */
    public static void awaitErrorCode(CompletionStage<?> stage, int expectedErrorCode, Duration timeout) {
        try {
            get(stage, timeout);
            String message = String.format("stage succeeded, expected a %d error", expectedErrorCode);
            fail(message);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            int errorCode = getErrorCode(cause);
            if (errorCode == expectedErrorCode) {
                return;
            }

            String message =
                    String.format("stage failed with a %d error, expected a %d error", errorCode, expectedErrorCode);
            fail(message, cause);
        } catch (TimeoutException | InterruptedException e) {
            fail(e);
        }
    }

    /** Converts an asynchronous stage to an asynchronous Retrofit call. */
    public static <V> Call<V> toCall(CompletionStage<V> stage) {
        return toCall(stage, DEFAULT_TIMEOUT);
    }

    /** Converts an asynchronous stage to an asynchronous Retrofit call. */
    public static <V> Call<V> toCall(CompletionStage<V> stage, Duration timeout) {
        return Calls.defer(() -> completeAsCall(stage, timeout));
    }

    /** Wraps an asynchronous API, converting uncaught exceptions to a failed stage. */
    public static <V> CompletionStage<V> wrapExceptions(Supplier<CompletionStage<V>> api) {
        try {
            return api.get();
        } catch (RuntimeException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    /** Completes the asynchronous stage, converting the result to a Retrofit call. */
    private static <V> Call<V> completeAsCall(CompletionStage<V> stage, Duration timeout) {
        try {
            V value = get(stage, timeout);
            return Calls.response(value);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            int errorCode = getErrorCode(cause);
            Response<V> response = Response.error(errorCode, ResponseBody.create("", null));
            return Calls.response(response);
        } catch (TimeoutException | InterruptedException e) {
            return Calls.failure(e);
        }
    }

    /** Gets the result of an asynchronous stage, subject to a timeout. */
    private static <V> V get(CompletionStage<V> stage, Duration timeout)
            throws ExecutionException, TimeoutException, InterruptedException {
        return stage.toCompletableFuture().get(timeout.toMillis(), TimeUnit.MILLISECONDS);
    }

    /** Gets the HTTP error code. */
    private static int getErrorCode(Throwable t) {
        return (t instanceof WebApplicationException e) ? e.getResponse().getStatus() : 500;
    }

    private WebStageTesting() {} // static class
}
