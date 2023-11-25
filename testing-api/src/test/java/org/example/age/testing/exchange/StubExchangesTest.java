package org.example.age.testing.exchange;

import static org.assertj.core.api.Assertions.assertThat;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class StubExchangesTest {

    @Test
    public void requestHeaders() {
        HttpServerExchange exchange = StubExchanges.create(Map.of("User-Agent", "agent"));
        String userAgent = exchange.getRequestHeaders().getFirst(Headers.USER_AGENT);
        assertThat(userAgent).isEqualTo("agent");
    }
}
