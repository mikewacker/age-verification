package org.example.age.common.api.data;

import io.undertow.server.HttpServerExchange;
import java.util.Optional;
import org.example.age.api.Sender;

/**
 * Extracts an account ID from an {@link HttpServerExchange}, or sends an error status code.
 *
 * <p>The account ID could be a username, or it could be an ephemeral ID for a session.</p>
 */
@FunctionalInterface
public interface AccountIdExtractor {

    Optional<String> tryExtract(HttpServerExchange exchange, Sender sender);
}
