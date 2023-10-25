package org.example.age.common.utils.testing;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import java.nio.charset.StandardCharsets;
import org.example.age.common.utils.internal.ExchangeUtils;
import org.example.age.common.utils.internal.HttpOptional;

/** Adds an operand in a query parameter and an operand in the body. Also has some special cases. */
public final class TestAddHandler implements HttpHandler {

    public static HttpHandler create() {
        return new TestAddHandler();
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        ExchangeUtils.handleRequestWithBody(exchange, TestAddHandler::deserialize, TestAddHandler::handleAddRequest);
    }

    private static void handleAddRequest(HttpServerExchange exchange, int operand2) {
        int operand1 = Integer.valueOf(
                ExchangeUtils.tryGetQueryParameter(exchange, "operand").get());
        int sum = operand1 + operand2;
        HttpOptional<Integer> maybeSum = (sum != 42) ? HttpOptional.of(sum) : HttpOptional.empty(418);
        ExchangeUtils.sendResponse(exchange, "text/plain", maybeSum, TestAddHandler::serialize);
    }

    private static int deserialize(byte[] bytes) {
        return Integer.valueOf(new String(bytes, StandardCharsets.UTF_8));
    }

    private static byte[] serialize(int x) {
        return Integer.toString(x).getBytes(StandardCharsets.UTF_8);
    }

    private TestAddHandler() {}
}
