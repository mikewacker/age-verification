package org.example.age.testing.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import java.io.IOException;
import org.assertj.core.api.ThrowableAssert;
import org.example.age.api.HttpOptional;
import org.example.age.testing.client.TestClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class TestUndertowServerTest {

    @RegisterExtension
    private static final TestUndertowServer server =
            TestUndertowServer.fromHandlerAtPath(() -> TestUndertowServerTest::handleApiRequest, "/api/");

    @Test
    public void exchange_HandledPath() throws IOException {
        HttpOptional<String> maybeValue = TestClient.apiRequestBuilder()
                .get(server.url("/api/test"))
                .executeWithJsonResponse(new TypeReference<>() {});
        assertThat(maybeValue).hasValue("test");
    }

    @Test
    public void exchange_UnhandledPath() throws IOException {
        HttpOptional<String> maybeValue =
                TestClient.apiRequestBuilder().get(server.rootUrl()).executeWithJsonResponse(new TypeReference<>() {});
        assertThat(maybeValue).isEmptyWithErrorCode(404);
    }

    @Test
    public void getServer() {
        assertThat(server.get()).isNotNull();
    }

    @Test
    public void getLocation() {
        assertThat(server.host()).isEqualTo("localhost");
        assertThat(server.port()).isBetween(1024, 65535);
        String expectedUrl = String.format("http://localhost:%d", server.port());
        assertThat(server.rootUrl()).isEqualTo(expectedUrl);
    }

    @Test
    public void error_ServerNotStarted() {
        TestUndertowServer inactiveServer =
                TestUndertowServer.fromHandler(() -> TestUndertowServerTest::handleApiRequest);
        error_ServerNotStarted(inactiveServer::get);
        error_ServerNotStarted(inactiveServer::host);
        error_ServerNotStarted(inactiveServer::port);
        error_ServerNotStarted(inactiveServer::rootUrl);
    }

    private void error_ServerNotStarted(ThrowableAssert.ThrowingCallable callable) {
        assertThatThrownBy(callable).isInstanceOf(IllegalStateException.class).hasMessage("server has not started");
    }

    /** {@link HttpHandler} for API requests that sends a stub response. */
    private static void handleApiRequest(HttpServerExchange exchange) {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send("\"test\"");
    }
}
