package org.example.age.infra.api;

import static org.assertj.core.api.Assertions.assertThat;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import java.io.IOException;
import okhttp3.Response;
import org.example.age.api.Dispatcher;
import org.example.age.api.StatusCodeSender;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class ExchangeDispatcherTest {

    @RegisterExtension
    private static final TestUndertowServer server = TestUndertowServer.create(TestHandler::create);

    @Test
    public void dispatch() throws IOException {
        dispatch("/dispatch", 200);
    }

    @Test
    public void dispatched_IoThread() throws IOException {
        dispatch("/dispatched-io-thread", 200);
    }

    @Test
    public void dispatched_Worker() throws IOException {
        dispatch("/dispatched-worker", 200);
    }

    @Test
    public void error_dispatch() throws IOException {
        dispatch("/error-dispatch", 500);
    }

    @Test
    public void error_dispatched() throws IOException {
        dispatch("/error-dispatched", 500);
    }

    private void dispatch(String path, int expectedStatusCode) throws IOException {
        Response response = TestClient.get(server.url(path));
        assertThat(response.code()).isEqualTo(expectedStatusCode);
    }

    /** Test {@link HttpHandler} that uses an {@link ExchangeDispatcher}. */
    private static final class TestHandler implements HttpHandler {

        public static HttpHandler create() {
            return new TestHandler();
        }

        @Override
        public void handleRequest(HttpServerExchange exchange) {
            Dispatcher dispatcher = ExchangeDispatcher.create(exchange);
            StatusCodeSender sender = ExchangeStatusCodeSender.create(exchange);
            if (!dispatcher.isInIoThread()) {
                sender.send(418);
                return;
            }

            switch (exchange.getRequestPath()) {
                case "/dispatch" -> dispatcher.dispatch(sender, TestHandler::handler);
                case "/dispatched-io-thread" -> dispatchedIoThread(sender, dispatcher);
                case "/dispatched-worker" -> dispatchedWorker(sender, dispatcher);
                case "/error-dispatch" -> dispatcher.dispatch(sender, TestHandler::badHandler);
                case "/error-dispatched" -> dispatchedBadHandler(sender, dispatcher);
                default -> sender.send(StatusCodes.NOT_FOUND);
            }
        }

        private static void dispatchedIoThread(StatusCodeSender sender, Dispatcher dispatcher) {
            dispatcher.getIoThread().execute(() -> dispatcher.executeHandler(sender, TestHandler::ioThreadHandler));
            dispatcher.dispatched();
        }

        private static void dispatchedWorker(StatusCodeSender sender, Dispatcher dispatcher) {
            dispatcher.getWorker().execute(() -> dispatcher.executeHandler(sender, TestHandler::handler));
            dispatcher.dispatched();
        }

        private static void dispatchedBadHandler(StatusCodeSender sender, Dispatcher dispatcher) {
            dispatcher.getWorker().execute(() -> dispatcher.executeHandler(sender, TestHandler::badHandler));
            dispatcher.dispatched();
        }

        private static void handler(StatusCodeSender sender, Dispatcher dispatcher) {
            if (dispatcher.isInIoThread()) {
                sender.send(418);
                return;
            }

            sender.sendOk();
        }

        private static void ioThreadHandler(StatusCodeSender sender, Dispatcher dispatcher) {
            if (!dispatcher.isInIoThread()) {
                sender.send(418);
                return;
            }

            sender.sendOk();
        }

        private static void badHandler(StatusCodeSender sender, Dispatcher dispatcher) {
            throw new RuntimeException();
        }

        private TestHandler() {}
    }
}
