package org.example.age.api.infra;

import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import java.io.IOException;
import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.base.ValueSender;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.TestServer;
import org.example.age.testing.server.undertow.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class UndertowDispatcherTest {

    @RegisterExtension
    private static final TestServer<?> server = TestUndertowServer.register("test", TestHandler::create);

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

    private static HttpOptional<String> executeRequest(String path) throws IOException {
        return TestClient.requestBuilder(new TypeReference<String>() {})
                .get(server.url(path))
                .execute();
    }

    /** Test {@link HttpHandler} that uses an {@link UndertowDispatcher}. */
    private static final class TestHandler implements HttpHandler {

        public static HttpHandler create() {
            return new TestHandler();
        }

        @Override
        public void handleRequest(HttpServerExchange exchange) {
            Dispatcher dispatcher = UndertowDispatcher.create(exchange);
            ValueSender<String> sender = UndertowJsonValueSender.create(exchange);
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
                            .execute(() -> dispatcher.executeHandler(() -> ioThreadHandler(sender, dispatcher)));
                    dispatcher.dispatched();
                    return;
                case "/dispatched-worker":
                    dispatcher
                            .getWorker()
                            .execute(() -> dispatcher.executeHandler(() -> workerHandler(sender, dispatcher)));
                    dispatcher.dispatched();
                    return;
                case "/error-dispatch":
                    dispatcher.dispatch(sender, TestHandler::badHandler);
                    return;
                case "/error-dispatched":
                    dispatcher
                            .getWorker()
                            .execute(() -> dispatcher.executeHandler(() -> badHandler(sender, dispatcher)));
                    dispatcher.dispatched();
                    return;
                default:
                    sender.sendErrorCode(StatusCodes.NOT_FOUND);
            }
        }

        private static void workerHandler(ValueSender<String> sender, Dispatcher dispatcher) {
            if (dispatcher.isInIoThread()) {
                sender.sendErrorCode(418);
                return;
            }

            sender.sendValue("test");
        }

        private static void ioThreadHandler(ValueSender<String> sender, Dispatcher dispatcher) {
            if (!dispatcher.isInIoThread()) {
                sender.sendErrorCode(418);
                return;
            }

            sender.sendValue("test");
        }

        private static void badHandler(ValueSender<String> sender, Dispatcher dispatcher) {
            throw new RuntimeException();
        }

        private TestHandler() {}
    }
}
