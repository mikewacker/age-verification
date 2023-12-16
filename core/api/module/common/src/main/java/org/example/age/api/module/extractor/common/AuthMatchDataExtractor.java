package org.example.age.api.module.extractor.common;

import io.undertow.server.HttpServerExchange;
import org.example.age.api.adapter.Extractor;
import org.example.age.api.def.common.AuthMatchData;

/** Extracts {@link AuthMatchData} from an {@link HttpServerExchange}, or returns an error status code. */
@FunctionalInterface
public interface AuthMatchDataExtractor extends Extractor<HttpServerExchange, AuthMatchData> {}
