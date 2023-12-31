package org.example.age.api.infra;

import io.github.mikewacker.drift.json.JsonValues;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import java.nio.ByteBuffer;
import org.xnio.IoUtils;

/** Utilities for sending a response with Undertow, with protections against double-sending a response. */
final class UndertowResponses {

    /** Sends a status code. */
    public static void sendStatusCode(HttpServerExchange exchange, int statusCode) {
        if (!safeCheckResponseNotStarted(exchange)) {
            return;
        }

        exchange.setStatusCode(statusCode);
        exchange.endExchange();
    }

    /** Sends a value as JSON. */
    public static void sendJsonValue(HttpServerExchange exchange, Object value) {
        if (!safeCheckResponseNotStarted(exchange)) {
            return;
        }

        byte[] rawValue = JsonValues.serialize(value);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(ByteBuffer.wrap(rawValue));
    }

    /** Checks that the response has not started, safely closing the connection if it has started. */
    private static boolean safeCheckResponseNotStarted(HttpServerExchange exchange) {
        if (exchange.isResponseStarted()) {
            IoUtils.safeClose(exchange.getConnection());
            return false;
        }

        return true;
    }

    // static class
    private UndertowResponses() {}
}
