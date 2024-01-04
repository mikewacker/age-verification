package org.example.age.api.extractor;

import io.github.mikewacker.drift.endpoint.ArgExtractor;
import io.undertow.server.HttpServerExchange;

/**
 * Extracts an account ID from an {@link HttpServerExchange}, or returns an error status code.
 *
 * <p>The account ID could be a username, or it could be an ephemeral ID for a session.</p>
 */
@FunctionalInterface
public interface AccountIdExtractor extends ArgExtractor<HttpServerExchange, String> {}
