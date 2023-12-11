package org.example.age.module.extractor.common;

import io.undertow.server.HttpServerExchange;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.common.AuthMatchData;

/** Extracts {@link AuthMatchData} from an {@link HttpServerExchange}, or returns an error status code. */
@FunctionalInterface
public interface AuthMatchDataExtractor {

    HttpOptional<AuthMatchData> tryExtract(HttpServerExchange exchange);
}
