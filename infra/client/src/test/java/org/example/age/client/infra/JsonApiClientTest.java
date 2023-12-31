package org.example.age.client.infra;

import static io.github.mikewacker.drift.testing.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.mikewacker.drift.api.HttpOptional;
import io.github.mikewacker.drift.testing.server.MockServer;
import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class JsonApiClientTest {

    @RegisterExtension
    private static final MockServer mockServer = MockServer.register("test");

    @Test
    public void statusCodeResponse() throws IOException {
        mockServer.enqueue(new MockResponse());
        int statusCode =
                JsonApiClient.requestBuilder().get(mockServer.rootUrl()).build().execute();
        assertThat(statusCode).isEqualTo(200);
    }

    @Test
    public void jsonValueResponse() throws IOException {
        mockServer.enqueue(
                new MockResponse().setHeader("Content-Type", "application/json").setBody("\"test\""));
        HttpOptional<String> maybeText = JsonApiClient.requestBuilder(new TypeReference<String>() {})
                .get(mockServer.rootUrl())
                .build()
                .execute();
        assertThat(maybeText).hasValue("test");
    }

    @Test
    public void error_ExecuteTwice() throws IOException {
        mockServer.enqueue(new MockResponse());
        JsonApiClient.ExecuteStage<Integer> executor =
                JsonApiClient.requestBuilder().get(mockServer.rootUrl()).build();
        executor.execute();
        assertThatThrownBy(executor::execute)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("request was already executed");
    }
}
