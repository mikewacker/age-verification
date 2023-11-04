package org.example.age.testing.client;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.ServerConnection;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;
import java.util.Map;
import org.xnio.XnioExecutor;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;

/** Creates {@link HttpServerExchange}'s for testing. */
public final class TestExchanges {

    /**
     * Adds request headers to a mock exchange.
     *
     * <p>A header will be omitted if the value is an empty string.</p>
     */
    public static void addRequestHeaders(HttpServerExchange exchange, Map<HttpString, String> headers) {
        HeaderMap headerMap = new HeaderMap();
        for (HttpString name : headers.keySet()) {
            String value = headers.get(name);
            if (value.isEmpty()) {
                continue;
            }

            headerMap.put(name, value);
        }
        when(exchange.getRequestHeaders()).thenReturn(headerMap);
    }

    /** Adds a stub IO thread to a mock exchange. */
    public static void addStubIoThread(HttpServerExchange exchange) {
        XnioIoThread ioThread = mock(XnioIoThread.class);
        when(exchange.getIoThread()).thenReturn(ioThread);
        XnioExecutor.Key afterKey = mock(XnioExecutor.Key.class);
        when(ioThread.executeAfter(any(), anyLong(), any())).thenReturn(afterKey);
        XnioExecutor.Key intervalKey = mock(XnioExecutor.Key.class);
        when(ioThread.executeAtInterval(any(), anyLong(), any())).thenReturn(intervalKey);
    }

    /** Adds a stub worker to a mock exchange. */
    public static void addStubWorker(HttpServerExchange exchange) {
        ServerConnection connection = mock(ServerConnection.class);
        when(exchange.getConnection()).thenReturn(connection);
        XnioWorker worker = mock(XnioWorker.class);
        when(connection.getWorker()).thenReturn(worker);
    }

    // static class
    private TestExchanges() {}
}
