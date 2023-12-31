package org.example.age.api.infra;

import static io.github.mikewacker.drift.testing.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.mikewacker.drift.api.HttpOptional;
import io.github.mikewacker.drift.testing.server.TestServer;
import io.github.mikewacker.drift.testing.server.TestUndertowServer;
import java.io.IOException;
import org.example.age.client.infra.JsonApiClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class UndertowJsonApiHandlerTest {

    @RegisterExtension
    private static final TestServer<?> server = TestUndertowServer.register("test", "/api/", AddService::createHandler);

    @Test
    public void exchange_StatusCode() throws IOException {
        int statusCode = executeHealthRequest();
        assertThat(statusCode).isEqualTo(200);
    }

    @Test
    public void exchange_JsonValue() throws IOException {
        HttpOptional<Integer> maybeSum = executeAddRequest("/api/add?operand=2", 2);
        assertThat(maybeSum).hasValue(4);
    }

    @Test
    public void exchange_NotFound() throws IOException {
        int statusCode = executeRequestAtBadPath();
        assertThat(statusCode).isEqualTo(404);
    }

    @Test
    public void error_InvalidBody() throws IOException {
        HttpOptional<Integer> maybeSum = executeAddRequest("/api/add?operand=2", "a");
        assertThat(maybeSum).isEmptyWithErrorCode(400);
    }

    @Test
    public void error_MissingParam() throws IOException {
        HttpOptional<Integer> maybeSum = executeAddRequest("/api/add", 2);
        assertThat(maybeSum).isEmptyWithErrorCode(400);
    }

    @Test
    public void error_InvalidParam() throws IOException {
        HttpOptional<Integer> maybeSum = executeAddRequest("/api/add?operand=a", 2);
        assertThat(maybeSum).isEmptyWithErrorCode(400);
    }

    @Test
    public void error_UncaughtExceptionInHandler() throws IOException {
        HttpOptional<Integer> maybeSum = executeAddRequest("/api/add?operand=200", 300);
        assertThat(maybeSum).isEmptyWithErrorCode(500);
    }

    private static HttpOptional<Integer> executeAddRequest(String path, Object bodyOperand) throws IOException {
        return JsonApiClient.requestBuilder(new TypeReference<Integer>() {})
                .post(server.url(path))
                .body(bodyOperand)
                .build()
                .execute();
    }

    private static int executeHealthRequest() throws IOException {
        return JsonApiClient.requestBuilder()
                .get(server.url("/api/health"))
                .build()
                .execute();
    }

    private static int executeRequestAtBadPath() throws IOException {
        return JsonApiClient.requestBuilder()
                .get(server.url("/api/dne"))
                .build()
                .execute();
    }
}
