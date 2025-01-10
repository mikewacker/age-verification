package org.example.age.api.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.ws.rs.WebApplicationException;
import java.io.IOException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Test;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.mock.Calls;

public final class AsyncCallsTest {

    @Test
    public void successfulResponse() {
        Call<String> call = Calls.response("test");
        CompletionStage<String> asyncValue = AsyncCalls.make(call);
        assertThat(asyncValue).isCompletedWithValue("test");
    }

    @Test
    public void unsuccessfulResponse() {
        Call<Integer> call = Calls.response(Response.error(400, ResponseBody.create("", null)));
        CompletionStage<Integer> asyncValue = AsyncCalls.make(call);
        assertThatThrownBy(() -> asyncValue.toCompletableFuture().get())
                .isInstanceOf(ExecutionException.class)
                .hasCauseInstanceOf(WebApplicationException.class);
    }

    @Test
    public void failedResponse() {
        Call<Integer> call = Calls.failure(new IOException());
        CompletionStage<Integer> asyncValue = AsyncCalls.make(call);
        assertThat(asyncValue).isCompletedExceptionally();
        assertThatThrownBy(() -> asyncValue.toCompletableFuture().get())
                .isInstanceOf(ExecutionException.class)
                .hasCauseInstanceOf(IOException.class);
    }
}
