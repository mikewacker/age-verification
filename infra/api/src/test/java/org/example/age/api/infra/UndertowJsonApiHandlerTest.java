package org.example.age.api.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import org.example.age.api.base.HttpOptional;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.TestServer;
import org.example.age.testing.server.undertow.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class UndertowJsonApiHandlerTest {

    @RegisterExtension
    private static final TestServer<?> server = TestUndertowServer.register("test", AddHandler::create);

    @Test
    public void exchange_StatusCode() throws IOException {
        int statusCode = executeHealthRequest();
        assertThat(statusCode).isEqualTo(200);
    }

    @Test
    public void exchange_JsonValue() throws IOException {
        HttpOptional<Integer> maybeSum = executeAddRequest("/add?operand=2", 2);
        assertThat(maybeSum).hasValue(4);
    }

    @Test
    public void exchange_NotFound() throws IOException {
        int statusCode = executeRequestAtBadPath();
        assertThat(statusCode).isEqualTo(404);
    }

    @Test
    public void error_InvalidBody() throws IOException {
        HttpOptional<Integer> maybeSum = executeAddRequest("/add?operand=2", "a");
        assertThat(maybeSum).isEmptyWithErrorCode(400);
    }

    @Test
    public void error_MissingParam() throws IOException {
        HttpOptional<Integer> maybeSum = executeAddRequest("/add", 2);
        assertThat(maybeSum).isEmptyWithErrorCode(400);
    }

    @Test
    public void error_InvalidParam() throws IOException {
        HttpOptional<Integer> maybeSum = executeAddRequest("/add?operand=a", 2);
        assertThat(maybeSum).isEmptyWithErrorCode(400);
    }

    @Test
    public void error_UncaughtExceptionInHandler() throws IOException {
        HttpOptional<Integer> maybeSum = executeAddRequest("/add?operand=200", 300);
        assertThat(maybeSum).isEmptyWithErrorCode(500);
    }

    private static HttpOptional<Integer> executeAddRequest(String path, Object bodyOperand) throws IOException {
        return TestClient.requestBuilder(new TypeReference<Integer>() {})
                .post(server.url(path))
                .body(bodyOperand)
                .execute();
    }

    private static int executeHealthRequest() throws IOException {
        return TestClient.requestBuilder().get(server.url("/health")).execute();
    }

    private static int executeRequestAtBadPath() throws IOException {
        return TestClient.requestBuilder().get(server.url("/dne")).execute();
    }
}
