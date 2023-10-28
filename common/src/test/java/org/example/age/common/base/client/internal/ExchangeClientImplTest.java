package org.example.age.common.base.client.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import io.undertow.server.HttpServerExchange;
import okhttp3.OkHttpClient;
import org.example.age.testing.TestExchanges;
import org.junit.jupiter.api.Test;

public final class ExchangeClientImplTest {

    @Test
    public void getInstance() {
        ExchangeClient client = new ExchangeClientImpl();
        HttpServerExchange exchange = createStubExchange();
        OkHttpClient client1 = client.getInstance(exchange);
        OkHttpClient client2 = client.getInstance(exchange);
        assertThat(client1).isSameAs(client2);
    }

    private static HttpServerExchange createStubExchange() {
        HttpServerExchange exchange = mock(HttpServerExchange.class);
        TestExchanges.addStubWorker(exchange);
        return exchange;
    }
}
