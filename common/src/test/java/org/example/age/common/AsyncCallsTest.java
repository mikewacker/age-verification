package org.example.age.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.util.WebStageTesting.await;
import static org.example.age.testing.util.WebStageTesting.awaitErrorCode;

import java.io.IOException;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Test;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.mock.Calls;

public final class AsyncCallsTest {

    @Test
    public void successfulResponse() {
        Call<String> call = Calls.response("test");
        String value = await(AsyncCalls.make(call));
        assertThat(value).isEqualTo("test");
    }

    @Test
    public void unsuccessfulResponse() {
        Call<Integer> call = Calls.response(Response.error(400, ResponseBody.create("", null)));
        awaitErrorCode(AsyncCalls.make(call), 400);
    }

    @Test
    public void failedResponse() {
        Call<Integer> call = Calls.failure(new IOException());
        awaitErrorCode(AsyncCalls.make(call), 500);
    }
}
