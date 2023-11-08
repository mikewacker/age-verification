package org.example.age.common.api;

import static org.assertj.core.api.Assertions.assertThat;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import java.io.IOException;
import okhttp3.Response;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class ExecutorContextTest {

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

    /** Test {@link HttpHandler} that uses the {@link ExchangeExecutors}. */
    private static final class TestHandler implements HttpHandler {

        public static HttpHandler create() {
            return new TestHandler();
        }

        @Override
        public void handleRequest(HttpServerExchange exchange) {
            ExchangeExecutors executors = ExchangeExecutors.create(exchange);
            CodeSender sender = CodeSender.create(exchange);
            if (!executors.isInIoThread()) {
                sender.send(418);
                return;
            }

            switch (exchange.getRequestPath()) {
                case "/dispatch" -> executors.dispatch(sender, TestHandler::handler);
                case "/dispatched-io-thread" -> dispatchedIoThread(executors, sender);
                case "/dispatched-worker" -> dispatchedWorker(executors, sender);
                case "/error-dispatch" -> executors.dispatch(sender, TestHandler::badHandler);
                case "/error-dispatched" -> dispatchedBadHandler(executors, sender);
                default -> sender.send(StatusCodes.NOT_FOUND);
            }
        }

        private static void dispatchedIoThread(ExchangeExecutors executors, CodeSender sender) {
            executors.getIoThread().execute(() -> executors.executeHandler(sender, TestHandler::ioThreadHandler));
            executors.dispatched();
        }

        private static void dispatchedWorker(ExchangeExecutors executors, CodeSender sender) {
            executors.getWorker().execute(() -> executors.executeHandler(sender, TestHandler::handler));
            executors.dispatched();
        }

        private static void dispatchedBadHandler(ExchangeExecutors executors, CodeSender sender) {
            executors.getWorker().execute(() -> executors.executeHandler(sender, TestHandler::badHandler));
            executors.dispatched();
        }

        private static void handler(ExchangeExecutors executors, CodeSender sender) {
            if (executors.isInIoThread()) {
                sender.send(418);
                return;
            }

            sender.sendOk();
        }

        private static void ioThreadHandler(ExchangeExecutors executors, CodeSender sender) {
            if (!executors.isInIoThread()) {
                sender.send(418);
                return;
            }

            sender.sendOk();
        }

        private static void badHandler(ExchangeExecutors executors, CodeSender sender) {
            throw new RuntimeException();
        }

        private TestHandler() {}
    }
}
