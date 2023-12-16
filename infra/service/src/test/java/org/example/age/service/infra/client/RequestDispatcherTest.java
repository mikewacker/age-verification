package org.example.age.service.infra.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.SocketPolicy;
import org.example.age.api.base.HttpOptional;
import org.example.age.testing.client.TestClient;
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
    public void backendRequest_StatusCodeResponse_Ok() throws IOException {
        backendServer.enqueue(new MockResponse());
        int statusCode = executeRequestWithStatusCodeResponse();
        assertThat(statusCode).isEqualTo(200);
    }

    @Test
    public void backendRequest_StatusCodeResponse_ErrorCode() throws IOException {
        backendServer.enqueue(new MockResponse().setResponseCode(403));
        int statusCode = executeRequestWithStatusCodeResponse();
        assertThat(statusCode).isEqualTo(403);
    }

    @Test
    public void backendRequest_StatusCodeResponse_RequestFails() throws IOException {
        backendServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));
        int statusCode = executeRequestWithStatusCodeResponse();
        assertThat(statusCode).isEqualTo(502);
    }

    @Test
    public void backendRequest_JsonValueResponse_Ok() throws IOException {
        backendServer.enqueue(new MockResponse().setBody("\"test\""));
        HttpOptional<String> maybeText = executeRequestWithJsonValueResponse();
        assertThat(maybeText).hasValue("test");
    }

    @Test
    public void backendRequest_JsonValueResponse_ErrorCode() throws IOException {
        backendServer.enqueue(new MockResponse().setResponseCode(403));
        HttpOptional<String> maybeText = executeRequestWithJsonValueResponse();
        assertThat(maybeText).isEmptyWithErrorCode(403);
    }

    @Test
    public void backendRequest_JsonValueResponse_RequestFails() throws IOException {
        backendServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));
        HttpOptional<String> maybeText = executeRequestWithJsonValueResponse();
        assertThat(maybeText).isEmptyWithErrorCode(502);
    }

    @Test
    public void backendRequest_JsonValueResponse_ReadResponseBodyFails() throws IOException {
        backendServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_DURING_RESPONSE_BODY));
        HttpOptional<String> maybeText = executeRequestWithJsonValueResponse();
        assertThat(maybeText).isEmptyWithErrorCode(502);
    }

    @Test
    public void backendRequest_JsonValueResponse_DeserializeResponseBodyFails() throws IOException {
        backendServer.enqueue(new MockResponse().setBody("{"));
        HttpOptional<String> maybeText = executeRequestWithJsonValueResponse();
        assertThat(maybeText).isEmptyWithErrorCode(502);
    }

    private int executeRequestWithStatusCodeResponse() throws IOException {
        return TestClient.requestBuilder()
                .get(frontendServer.url("/status-code"))
                .execute();
    }

    private HttpOptional<String> executeRequestWithJsonValueResponse() throws IOException {
        return TestClient.requestBuilder(new TypeReference<String>() {})
                .get(frontendServer.url("/text"))
                .execute();
    }
}
