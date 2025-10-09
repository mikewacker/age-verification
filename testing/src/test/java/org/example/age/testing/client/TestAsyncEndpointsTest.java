package org.example.age.testing.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.client.WebStageTesting.await;
import static org.example.age.testing.client.WebStageTesting.awaitErrorCode;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.junit.jupiter.api.Test;
import retrofit2.Call;
import retrofit2.Response;

public final class TestAsyncEndpointsTest {

    @Test
    public void test_Success() {
        ExampleApi endpoint = TestAsyncEndpoints.test(new MathEndpoint(), ExampleApi.class);
        int quotient = await(endpoint.divide(4, 2));
        assertThat(quotient).isEqualTo(2);
    }

    @Test
    public void test_UncaughtException() {
        ExampleApi endpoint = TestAsyncEndpoints.test(new MathEndpoint(), ExampleApi.class);
        awaitErrorCode(endpoint.divide(1, 0), 500);
    }

    @Test
    public void client_Success() throws IOException {
        ExampleApi endpoint = TestAsyncEndpoints.test(new MathEndpoint(), ExampleApi.class);
        ExampleClient client = TestAsyncEndpoints.client(endpoint, ExampleApi.class, ExampleClient.class);
        Response<Integer> response = client.divide(4, 2).execute();
        assertThat(response.isSuccessful()).isTrue();
        assertThat(response.body()).isEqualTo(2);
    }

    @Test
    public void client_ErrorCode() throws IOException {
        ExampleApi endpoint = TestAsyncEndpoints.test(new MathEndpoint(), ExampleApi.class);
        ExampleClient client = TestAsyncEndpoints.client(endpoint, ExampleApi.class, ExampleClient.class);
        Response<Integer> response = client.divide(1, 0).execute();
        assertThat(response.code()).isEqualTo(500);
    }

    /** Example API for testing. */
    private interface ExampleApi {

        CompletionStage<Integer> divide(int dividend, int divisor);
    }

    /** Corresponding client interface for {@link ExampleApi}. */
    private interface ExampleClient {

        Call<Integer> divide(int dividend, int divisor);
    }

    /** Endpoint for {@link ExampleApi}. */
    private static final class MathEndpoint implements ExampleApi {

        @Override
        public CompletionStage<Integer> divide(int dividend, int divisor) {
            int quotient = dividend / divisor;
            return CompletableFuture.completedFuture(quotient);
        }
    }
}
