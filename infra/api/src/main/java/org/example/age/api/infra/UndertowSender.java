package org.example.age.api.infra;

import io.github.mikewacker.drift.api.HttpOptional;
import io.github.mikewacker.drift.api.Sender;
import io.undertow.server.HttpServerExchange;

/** {@link Sender} that is backed by an {@link HttpServerExchange}. */
interface UndertowSender {

    /** {@link Sender.StatusCode} that is backed by an {@link HttpServerExchange}. */
    final class StatusCode implements Sender.StatusCode {

        private final HttpServerExchange exchange;

        /** Creates a {@link Sender.StatusCode} from an {@link HttpServerExchange}. */
        public static Sender.StatusCode create(HttpServerExchange exchange) {
            return new UndertowSender.StatusCode(exchange);
        }

        @Override
        public void send(int statusCode) {
            UndertowResponses.sendStatusCode(exchange, statusCode);
        }

        private StatusCode(HttpServerExchange exchange) {
            this.exchange = exchange;
        }
    }

    /** {@link Sender.Value} that is backed by an {@link HttpServerExchange}. It serializes values as JSON. */
    final class JsonValue<V> implements Sender.Value<V> {

        private final HttpServerExchange exchange;

        /** Creates a {@link Sender.Value} from an {@link HttpServerExchange}. */
        public static <V> Sender.Value<V> create(HttpServerExchange exchange) {
            return new UndertowSender.JsonValue<>(exchange);
        }

        @Override
        public void send(HttpOptional<V> maybeValue) {
            if (maybeValue.isEmpty()) {
                UndertowResponses.sendStatusCode(exchange, maybeValue.statusCode());
                return;
            }
            V value = maybeValue.get();

            UndertowResponses.sendJsonValue(exchange, value);
        }

        private JsonValue(HttpServerExchange exchange) {
            this.exchange = exchange;
        }
    }
}
