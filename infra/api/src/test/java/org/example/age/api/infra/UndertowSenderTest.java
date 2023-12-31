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

public final class UndertowSenderTest {

    @RegisterExtension
    private static final TestServer<?> server = TestUndertowServer.register("test", UndertowSenderHandler::create);

    @Test
    public void send_StatusCode_Ok() throws IOException {
        int statusCode = executeStatusCodeRequest("/status-code/ok");
        assertThat(statusCode).isEqualTo(200);
    }

    @Test
    public void send_StatusCode_Forbidden() throws IOException {
        int statusCode = executeStatusCodeRequest("/status-code/forbidden");
        assertThat(statusCode).isEqualTo(403);
    }

    @Test
    public void send_StatusCode_SendTwice() throws IOException {
        int statusCode = executeStatusCodeRequest("/status-code/send-twice");
        assertThat(statusCode).isEqualTo(200);
    }

    @Test
    public void send_JsonValue_Ok() throws IOException {
        HttpOptional<String> maybeText = executeTextRequest("/text/ok");
        assertThat(maybeText).hasValue("test");
    }

    @Test
    public void send_JsonValue_Forbidden() throws IOException {
        HttpOptional<String> maybeText = executeTextRequest("/text/forbidden");
        assertThat(maybeText).isEmptyWithErrorCode(403);
    }

    @Test
    public void send_JsonValue_SendTwice() throws IOException {
        HttpOptional<String> maybeText = executeTextRequest("/text/send-twice");
        assertThat(maybeText).hasValue("first");
    }

    private static int executeStatusCodeRequest(String path) throws IOException {
        return JsonApiClient.requestBuilder().get(server.url(path)).build().execute();
    }

    private static HttpOptional<String> executeTextRequest(String path) throws IOException {
        return JsonApiClient.requestBuilder(new TypeReference<String>() {})
                .get(server.url(path))
                .build()
                .execute();
    }
}
