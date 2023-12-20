package org.example.age.api.extractor;

import io.undertow.server.HttpServerExchange;
import org.example.age.api.adapter.Extractor;

/**
 * Extracts an account ID from an {@link HttpServerExchange}, or returns an error status code.
 *
 * <p>The account ID could be a username, or it could be an ephemeral ID for a session.</p>
 */
@FunctionalInterface
public interface AccountIdExtractor extends Extractor<HttpServerExchange, String> {}
