package org.example.age.testing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import jakarta.ws.rs.WebApplicationException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.mock.Calls;

/** Test utilities for {@link CompletionStage}. */
public final class CompletionStageTesting {

    /**
     * Asserts that the {@link CompletionStage} completes exceptionally
     * with a {@link WebApplicationException} that has the expected error code.
     */
    public static void assertIsCompletedWithErrorCode(CompletionStage<?> stage, int expectedErrorCode) {
        assertThat(stage).isCompletedExceptionally();
        try {
            stage.toCompletableFuture().get();
            fail();
        } catch (ExecutionException execE) {
            assertThat(execE.getCause()).isInstanceOf(WebApplicationException.class);
            WebApplicationException e = (WebApplicationException) execE.getCause();
            assertThat(e.getResponse().getStatus()).isEqualTo(expectedErrorCode);
        } catch (Exception e) {
            fail();
        }
    }

    /** Converts a {@link CompletionStage} to a {@link Call}. */
    public static <V> Call<V> toCall(CompletionStage<V> stage) {
        return Calls.defer(() -> completeAsCall(stage));
    }

    /** Completes the {@link CompletionStage}, converting the result to a {@link Call}. */
    private static <V> Call<V> completeAsCall(CompletionStage<V> stage) {
        try {
            V value = stage.toCompletableFuture().get();
            return Calls.response(value);
        } catch (ExecutionException execE) {
            if (!(execE.getCause() instanceof WebApplicationException e)) {
                return Calls.failure(execE.getCause());
            }

            int errorCode = e.getResponse().getStatus();
            Response<V> response = Response.error(errorCode, ResponseBody.create("", null));
            return Calls.response(response);
        } catch (Exception e) {
            return Calls.failure(e);
        }
    }

    // static class
    private CompletionStageTesting() {}
}
