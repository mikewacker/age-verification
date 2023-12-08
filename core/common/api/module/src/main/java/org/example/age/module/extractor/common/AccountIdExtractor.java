package org.example.age.module.extractor.common;

import io.undertow.server.HttpServerExchange;
import org.example.age.api.HttpOptional;

/**
 * Extracts an account ID from an {@link HttpServerExchange}, or returns an error status code.
 *
 * <p>The account ID could be a username, or it could be an ephemeral ID for a session.</p>
 */
@FunctionalInterface
public interface AccountIdExtractor {

    HttpOptional<String> tryExtract(HttpServerExchange exchange);
}
