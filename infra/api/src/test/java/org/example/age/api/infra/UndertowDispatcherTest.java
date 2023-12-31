package org.example.age.api.infra;

import static io.github.mikewacker.drift.testing.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.mikewacker.drift.api.HttpOptional;
import java.io.IOException;
import org.example.age.client.infra.JsonApiClient;
import org.example.age.testing.server.TestServer;
import org.example.age.testing.server.undertow.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class UndertowDispatcherTest {

    @RegisterExtension
    private static final TestServer<?> server = TestUndertowServer.register("test", UndertowDispatcherHandler::create);

    @Test
    public void dispatch() throws IOException {
        HttpOptional<String> maybeValue = executeRequest("/dispatch/ok");
        assertThat(maybeValue).hasValue("test");
    }

    @Test
    public void dispatch_UncaughtExceptionInHandler() throws IOException {
        HttpOptional<String> maybeValue = executeRequest("/dispatch/error");
        assertThat(maybeValue).isEmptyWithErrorCode(500);
    }

    @Test
    public void dispatched_Worker() throws IOException {
        HttpOptional<String> maybeValue = executeRequest("/dispatched/ok/worker");
        assertThat(maybeValue).hasValue("test");
    }

    @Test
    public void dispatched_IoThread() throws IOException {
        HttpOptional<String> maybeValue = executeRequest("/dispatched/ok/io-thread");
        assertThat(maybeValue).hasValue("test");
    }

    @Test
    public void dispatched_UncaughtExceptionInHandler() throws IOException {
        HttpOptional<String> maybeValue = executeRequest("/dispatched/error");
        assertThat(maybeValue).isEmptyWithErrorCode(500);
    }

    private static HttpOptional<String> executeRequest(String path) throws IOException {
        return JsonApiClient.requestBuilder(new TypeReference<String>() {})
                .get(server.url(path))
                .build()
                .execute();
    }
}
