package org.example.age.common.api.data;

import io.undertow.server.HttpServerExchange;
import org.example.age.api.HttpOptional;

/** Extracts {@link AuthMatchData} from an {@link HttpServerExchange}, or returns an error status code. */
@FunctionalInterface
public interface AuthMatchDataExtractor {

    HttpOptional<AuthMatchData> tryExtract(HttpServerExchange exchange);
}
