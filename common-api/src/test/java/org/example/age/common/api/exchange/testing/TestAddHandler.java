package org.example.age.common.api.exchange.testing;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.example.age.common.api.HttpOptional;
import org.example.age.common.api.exchange.impl.ExchangeUtils;

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
        Optional<Integer> maybeOperand1 = ExchangeUtils.tryGetQueryParameter(exchange, "operand", Integer::valueOf);
        if (maybeOperand1.isEmpty()) {
            ExchangeUtils.sendStatusCode(exchange, StatusCodes.BAD_REQUEST);
            return;
        }

        int operand1 = maybeOperand1.get();
        int sum = operand1 + operand2;
        if (sum == 500) {
            throw new RuntimeException();
        }

        HttpOptional<Integer> maybeSum = (sum != 418) ? HttpOptional.of(sum) : HttpOptional.empty(418);
        ExchangeUtils.sendResponse(exchange, "text/plain", maybeSum, TestAddHandler::serialize);
    }

    private static int deserialize(byte[] bytes) {
        return Integer.valueOf(new String(bytes, StandardCharsets.UTF_8));
    }

    private static byte[] serialize(int x) {
        if (x == 1337) {
            throw new RuntimeException();
        }

        return Integer.toString(x).getBytes(StandardCharsets.UTF_8);
    }

    private TestAddHandler() {}
}
