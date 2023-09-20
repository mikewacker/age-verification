package org.example.age.common.verification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import org.junit.jupiter.api.Test;

public final class AuthDataTest {

    @Test
    public void match_UserAgentMatches() {
        HttpServerExchange exchange = createExchange("user agent");
        AuthData data1 = AuthData.create(exchange);
        AuthData data2 = AuthData.create(exchange);
        assertThat(data1.match(data2)).isTrue();
    }

    @Test
    public void match_UserAgentDoesNotMatch() {
        HttpServerExchange exchange1 = createExchange("user agent 1");
        AuthData data1 = AuthData.create(exchange1);
        HttpServerExchange exchange2 = createExchange("user agent 2");
        AuthData data2 = AuthData.create(exchange2);
        assertThat(data1.match(data2)).isFalse();
    }

    @Test
    public void match_UserAgentNotPresent() {
        HttpServerExchange exchange = createExchange(null);
        AuthData data1 = AuthData.create(exchange);
        AuthData data2 = AuthData.create(exchange);
        assertThat(data1.match(data2)).isTrue();
    }

    @Test
    public void serializeThenDeserialize() {
        HttpServerExchange exchange = createExchange("user agent");
        AuthData data = AuthData.create(exchange);
        byte[] bytes = data.serialize();
        AuthData deserializedData = AuthData.deserialize(bytes);
        assertThat(deserializedData.match(data)).isTrue();
    }

    private static HttpServerExchange createExchange(String userAgent) {
        HeaderMap headerMap = new HeaderMap();
        headerMap.put(Headers.USER_AGENT, userAgent);
        HttpServerExchange exchange = mock(HttpServerExchange.class);
        when(exchange.getRequestHeaders()).thenReturn(headerMap);
        return exchange;
    }
}
