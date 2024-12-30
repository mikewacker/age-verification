package org.example.age.service.util;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.ws.rs.WebApplicationException;
import java.io.IOException;
import java.util.concurrent.CompletionStage;
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
        assertThat(asyncValue).isCompletedExceptionally();
        assertThat(asyncValue.exceptionally(AsyncCallsTest::getErrorCode)).isCompletedWithValue(500);
    }

    @Test
    public void unsuccessfulResponse_MapErrorCode() {
        Call<Integer> call = Calls.response(Response.error(401, ResponseBody.create("", null)));
        CompletionStage<Integer> asyncValue = AsyncCalls.make(call, code -> code / 100 * 100);
        assertThat(asyncValue).isCompletedExceptionally();
        assertThat(asyncValue.exceptionally(AsyncCallsTest::getErrorCode)).isCompletedWithValue(400);
    }

    @Test
    public void failedResponse() {
        Call<Integer> call = Calls.failure(new IOException());
        CompletionStage<Integer> asyncValue = AsyncCalls.make(call);
        assertThat(asyncValue).isCompletedExceptionally();
        assertThat(asyncValue.exceptionally(AsyncCallsTest::getErrorCode)).isCompletedWithValue(500);
    }

    private static int getErrorCode(Throwable t) {
        WebApplicationException e = (WebApplicationException) t;
        return e.getResponse().getStatus();
    }
}
