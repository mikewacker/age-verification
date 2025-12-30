package org.example.age.testing.client;

import static org.assertj.core.api.Assertions.fail;

import jakarta.ws.rs.WebApplicationException;
import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
