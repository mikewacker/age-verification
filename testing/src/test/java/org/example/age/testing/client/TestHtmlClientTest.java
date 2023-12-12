package org.example.age.testing.client;

import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import java.io.IOException;
import org.example.age.api.base.HttpOptional;
import org.example.age.testing.server.TestServer;
import org.example.age.testing.server.undertow.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class TestHtmlClientTest {

    @RegisterExtension
    private static final TestServer<?> server = TestUndertowServer.register("test", TestHandler::create);

    @Test
    public void exchange_Ok() throws IOException {
        HttpOptional<String> maybeHtml = TestHtmlClient.get(server.url("/greeting.html"));
        assertThat(maybeHtml).hasValue("<p>Hello, world!</p>");
    }

    @Test
    public void exchange_ErrorCode() throws IOException {
        HttpOptional<String> maybeHtml = TestHtmlClient.get(server.url("/not-found.html"));
        assertThat(maybeHtml).isEmptyWithErrorCode(404);
    }

    /** Test {@link HttpHandler} for HTML. */
    private static final class TestHandler implements HttpHandler {

        public static HttpHandler create() {
            return new TestHandler();
        }

        @Override
        public void handleRequest(HttpServerExchange exchange) {
            switch (exchange.getRequestPath()) {
                case "/greeting.html":
                    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
                    exchange.getResponseSender().send("<p>Hello, world!</p>");
                    return;
                default:
                    exchange.setStatusCode(StatusCodes.NOT_FOUND);
            }
        }

        private TestHandler() {}
    }
}
