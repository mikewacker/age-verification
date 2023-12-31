package org.example.age.service.infra.client;

import static io.github.mikewacker.drift.testing.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.mikewacker.drift.api.HttpOptional;
import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.SocketPolicy;
import org.example.age.client.infra.JsonApiClient;
import org.example.age.testing.server.TestServer;
import org.example.age.testing.server.mock.MockServer;
import org.example.age.testing.server.undertow.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class RequestDispatcherTest {

    @RegisterExtension
    private static final TestServer<?> frontendServer =
            TestUndertowServer.register("frontend", ProxyService::createHandler);

    @RegisterExtension
    private static final MockServer backendServer = MockServer.register("backend");

    @Test
    public void backendRequest_StatusCodeResponse() throws IOException {
        backendServer.enqueue(new MockResponse());
        int statusCode = executeRequestWithStatusCodeResponse();
        assertThat(statusCode).isEqualTo(200);
    }

    @Test
    public void backendRequest_JsonValueResponse() throws IOException {
        backendServer.enqueue(
                new MockResponse().setHeader("Content-Type", "application/json").setBody("\"test\""));
        HttpOptional<String> maybeText = executeRequestWithJsonValueResponse();
        assertThat(maybeText).hasValue("test");
    }

    @Test
    public void backendRequest_RequestFails() throws IOException {
        backendServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));
        int statusCode = executeRequestWithStatusCodeResponse();
        assertThat(statusCode).isEqualTo(502);
    }

    @Test
    public void backendRequest_InvalidResponse() throws IOException {
        backendServer.enqueue(new MockResponse());
        HttpOptional<String> maybeText = executeRequestWithJsonValueResponse();
        assertThat(maybeText).isEmptyWithErrorCode(502);
    }

    private int executeRequestWithStatusCodeResponse() throws IOException {
        return JsonApiClient.requestBuilder()
                .get(frontendServer.url("/status-code"))
                .build()
                .execute();
    }

    private HttpOptional<String> executeRequestWithJsonValueResponse() throws IOException {
        return JsonApiClient.requestBuilder(new TypeReference<String>() {})
                .get(frontendServer.url("/text"))
                .build()
                .execute();
    }
}
