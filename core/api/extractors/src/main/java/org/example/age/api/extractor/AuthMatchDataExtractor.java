package org.example.age.api.extractor;

import io.github.mikewacker.drift.endpoint.ArgExtractor;
import io.undertow.server.HttpServerExchange;
import org.example.age.api.def.AuthMatchData;

/** Extracts {@link AuthMatchData} from an {@link HttpServerExchange}, or returns an error status code. */
@FunctionalInterface
public interface AuthMatchDataExtractor extends ArgExtractor<HttpServerExchange, AuthMatchData> {}
