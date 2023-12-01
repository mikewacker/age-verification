package org.example.age.infra.api;

import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import java.io.IOException;
import org.example.age.api.Dispatcher;
import org.example.age.api.HttpOptional;
import org.example.age.api.JsonSender;
import org.example.age.api.JsonSerializer;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class ExchangeDispatcherTest {

    @RegisterExtension
    private static final TestUndertowServer server = TestUndertowServer.fromHandler(TestHandler::create);

    @Test
    public void dispatch() throws IOException {
        HttpOptional<String> maybeValue = executeRequest("/dispatch");
        assertThat(maybeValue).hasValue("test");
    }

    @Test
    public void dispatched_IoThread() throws IOException {
        HttpOptional<String> maybeValue = executeRequest("/dispatched-io-thread");
        assertThat(maybeValue).hasValue("test");
    }

    @Test
    public void dispatched_Worker() throws IOException {
        HttpOptional<String> maybeValue = executeRequest("/dispatched-worker");
        assertThat(maybeValue).hasValue("test");
    }

    @Test
    public void error_dispatch() throws IOException {
        HttpOptional<String> maybeValue = executeRequest("/error-dispatch");
        assertThat(maybeValue).isEmptyWithErrorCode(500);
    }

    @Test
    public void error_dispatched() throws IOException {
        HttpOptional<String> maybeValue = executeRequest("/error-dispatched");
        assertThat(maybeValue).isEmptyWithErrorCode(500);
    }

    private HttpOptional<String> executeRequest(String path) throws IOException {
        return TestClient.apiRequestBuilder()
                .url(server.url(path))
                .get()
                .executeWithJsonResponse(new TypeReference<>() {});
    }

    /** Test {@link HttpHandler} that uses an {@link ExchangeDispatcher}. */
    private static final class TestHandler implements HttpHandler {

        private static final JsonSerializer serializer = JsonSerializer.create(new ObjectMapper());

        public static HttpHandler create() {
            return new TestHandler();
        }

        @Override
        public void handleRequest(HttpServerExchange exchange) {
            Dispatcher dispatcher = ExchangeDispatcher.create(exchange);
            JsonSender<String> sender = ExchangeJsonSender.create(exchange, serializer);
            if (!dispatcher.isInIoThread()) {
                sender.sendErrorCode(418);
                return;
            }

            switch (exchange.getRequestPath()) {
                case "/dispatch":
                    dispatcher.dispatch(sender, TestHandler::workerHandler);
                    return;
                case "/dispatched-io-thread":
                    dispatcher
                            .getIoThread()
                            .execute(() -> dispatcher.executeHandler(sender, TestHandler::ioThreadHandler));
                    dispatcher.dispatched();
                    return;
                case "/dispatched-worker":
                    dispatcher.getWorker().execute(() -> dispatcher.executeHandler(sender, TestHandler::workerHandler));
                    dispatcher.dispatched();
                    return;
                case "/error-dispatch":
                    dispatcher.dispatch(sender, TestHandler::badHandler);
                    return;
                case "/error-dispatched":
                    dispatcher.getWorker().execute(() -> dispatcher.executeHandler(sender, TestHandler::badHandler));
                    dispatcher.dispatched();
                    return;
                default:
                    sender.sendErrorCode(StatusCodes.NOT_FOUND);
            }
        }

        private static void workerHandler(JsonSender<String> sender, Dispatcher dispatcher) {
            if (dispatcher.isInIoThread()) {
                sender.sendErrorCode(418);
                return;
            }

            sender.sendBody("test");
        }

        private static void ioThreadHandler(JsonSender<String> sender, Dispatcher dispatcher) {
            if (!dispatcher.isInIoThread()) {
                sender.sendErrorCode(418);
                return;
            }

            sender.sendBody("test");
        }

        private static void badHandler(JsonSender<String> sender, Dispatcher dispatcher) {
            throw new RuntimeException();
        }

        private TestHandler() {}
    }
}
