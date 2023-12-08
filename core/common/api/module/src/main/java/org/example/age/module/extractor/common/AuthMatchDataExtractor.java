package org.example.age.module.extractor.common;

import io.undertow.server.HttpServerExchange;
import org.example.age.api.HttpOptional;
import org.example.age.common.api.data.AuthMatchData;

/** Extracts {@link AuthMatchData} from an {@link HttpServerExchange}, or returns an error status code. */
@FunctionalInterface
public interface AuthMatchDataExtractor {

    HttpOptional<AuthMatchData> tryExtract(HttpServerExchange exchange);
}
