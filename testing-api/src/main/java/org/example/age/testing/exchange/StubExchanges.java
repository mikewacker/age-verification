package org.example.age.testing.exchange;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;
import java.util.Map;

/** Creates stub {@link HttpServerExchange}'s. */
public final class StubExchanges {

    /** Creates a stub {@link HttpServerExchange}. */
    public static HttpServerExchange create() {
        return mock(HttpServerExchange.class);
    }

    /** Creates a stub {@link HttpServerExchange} with request headers. */
    public static HttpServerExchange create(Map<String, String> requestHeaders) {
        HttpServerExchange exchange = create();
        HeaderMap requestHeaderMap = new HeaderMap();
        requestHeaders.forEach((name, value) -> requestHeaderMap.put(HttpString.tryFromString(name), value));
        when(exchange.getRequestHeaders()).thenReturn(requestHeaderMap);
        return exchange;
    }

    // static class
    private StubExchanges() {}
}
