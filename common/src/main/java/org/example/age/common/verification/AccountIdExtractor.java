package org.example.age.common.verification;

import io.undertow.server.HttpServerExchange;
import java.util.Optional;

/** Extracts an account ID, if present, from an {@link HttpServerExchange}. */
@FunctionalInterface
public interface AccountIdExtractor {

    Optional<String> tryExtract(HttpServerExchange exchange);
}
