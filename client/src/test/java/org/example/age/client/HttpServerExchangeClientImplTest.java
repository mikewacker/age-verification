package org.example.age.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.ServerConnection;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;
import org.xnio.XnioWorker;

public final class HttpServerExchangeClientImplTest {

    @Test
    public void getInstance() {
        HttpServerExchangeClient client = new HttpServerExchangeClientImpl();
        HttpServerExchange exchange = createStubExchange();
        OkHttpClient client1 = client.getInstance(exchange);
        OkHttpClient client2 = client.getInstance(exchange);
        assertThat(client1).isSameAs(client2);
    }

    private static HttpServerExchange createStubExchange() {
        XnioWorker worker = mock(XnioWorker.class);
        ServerConnection connection = mock(ServerConnection.class);
        when(connection.getWorker()).thenReturn(worker);
        HttpServerExchange exchange = mock(HttpServerExchange.class);
        when(exchange.getConnection()).thenReturn(connection);
        return exchange;
    }
}
