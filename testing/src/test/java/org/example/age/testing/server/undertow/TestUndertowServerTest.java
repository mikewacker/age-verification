package org.example.age.testing.server.undertow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import java.io.IOException;
import org.example.age.api.HttpOptional;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.TestServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class TestUndertowServerTest {

    @RegisterExtension
    private static final TestServer<?> server =
            TestUndertowServer.register("test", () -> TestUndertowServerTest::handleApiRequest, "/api/");

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

    /** {@link HttpHandler} for API requests that sends a stub response. */
    private static void handleApiRequest(HttpServerExchange exchange) {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send("\"test\"");
    }
}
