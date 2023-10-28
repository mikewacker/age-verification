package org.example.age.common.base.account;

import io.undertow.server.HttpServerExchange;
import java.util.Optional;

/**
 * Extracts an account ID, if present, from an {@link HttpServerExchange}.
 *
 * <p>The account ID could be a username, or it could be an ephemeral ID for a session.</p>
 */
@FunctionalInterface
public interface AccountIdExtractor {

    Optional<String> tryExtract(HttpServerExchange exchange);
}
