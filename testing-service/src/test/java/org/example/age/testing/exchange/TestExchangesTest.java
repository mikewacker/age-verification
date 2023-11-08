package org.example.age.testing.exchange;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.xnio.XnioExecutor;
import org.xnio.XnioWorker;

@SuppressWarnings("DirectInvocationOnMock")
public final class TestExchangesTest {

    @Test
    public void addRequestHeaders() {
        HttpServerExchange exchange = mock(HttpServerExchange.class);
        TestExchanges.addRequestHeaders(exchange, Map.of(Headers.CONTENT_TYPE, "text/plain"));
        HeaderMap requestHeaders = exchange.getRequestHeaders();
        assertThat(requestHeaders.getFirst(Headers.CONTENT_TYPE)).isEqualTo("text/plain");
    }

    @Test
    public void addRequestHeaders_HeaderNotPresent() {
        HttpServerExchange exchange = mock(HttpServerExchange.class);
        TestExchanges.addRequestHeaders(exchange, Map.of(Headers.CONTENT_TYPE, ""));
        HeaderMap requestHeaders = exchange.getRequestHeaders();
        assertThat(requestHeaders.getFirst(Headers.CONTENT_TYPE)).isNull();
    }

    @Test
    public void addStubIoThread() {
        HttpServerExchange exchange = mock(HttpServerExchange.class);
        TestExchanges.addStubIoThread(exchange);
        XnioExecutor ioThread = exchange.getIoThread();
        ioThread.execute(TestExchangesTest::command);
        XnioExecutor.Key afterKey = ioThread.executeAfter(TestExchangesTest::command, 1, TimeUnit.SECONDS);
        afterKey.remove();
        XnioExecutor.Key intervalKey = ioThread.executeAtInterval(TestExchangesTest::command, 1, TimeUnit.SECONDS);
        intervalKey.remove();
    }

    @Test
    public void addStubWorker() {
        HttpServerExchange exchange = mock(HttpServerExchange.class);
        TestExchanges.addStubWorker(exchange);
        XnioWorker worker = exchange.getConnection().getWorker();
        worker.execute(TestExchangesTest::command);
    }

    private static void command() {}
}
