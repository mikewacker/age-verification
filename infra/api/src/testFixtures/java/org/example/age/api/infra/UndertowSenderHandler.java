package org.example.age.api.infra;

import io.github.mikewacker.drift.api.Sender;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;

/** Test {@link HttpHandler} that uses an {@link UndertowSender}. */
public final class UndertowSenderHandler implements HttpHandler {

    public static HttpHandler create() {
        return new UndertowSenderHandler();
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        Sender.StatusCode statusCodeSender = UndertowSender.StatusCode.create(exchange);
        Sender.Value<String> valueSender = UndertowSender.JsonValue.create(exchange);
        switch (exchange.getRequestPath()) {
            case "/status-code/ok" -> statusCodeSender.sendOk();
            case "/status-code/forbidden" -> statusCodeSender.sendErrorCode(StatusCodes.FORBIDDEN);
            case "/status-code/send-twice" -> sendStatusCodeTwice(statusCodeSender);
            case "/text/ok" -> valueSender.sendValue("test");
            case "/text/forbidden" -> valueSender.sendErrorCode(StatusCodes.FORBIDDEN);
            case "/text/send-twice" -> sendValueTwice(valueSender);
            default -> statusCodeSender.sendErrorCode(StatusCodes.NOT_FOUND);
        }
    }

    private void sendStatusCodeTwice(Sender.StatusCode sender) {
        sender.sendOk();
        sender.sendErrorCode(403);
    }

    private void sendValueTwice(Sender.Value<String> sender) {
        sender.sendValue("first");
        sender.sendValue("second");
    }

    private UndertowSenderHandler() {}
}
