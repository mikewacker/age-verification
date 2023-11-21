package org.example.age.common.service.data;

import io.undertow.server.HttpServerExchange;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.api.HttpOptional;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.common.api.data.AuthMatchDataExtractor;

/** {@link AuthMatchDataExtractor} that extracts {@link DisabledAuthMatchData}. */
@Singleton
final class DisabledAuthMatchDataExtractor implements AuthMatchDataExtractor {

    @Inject
    public DisabledAuthMatchDataExtractor() {}

    @Override
    public HttpOptional<AuthMatchData> tryExtract(HttpServerExchange exchange) {
        return HttpOptional.of(DisabledAuthMatchData.of());
    }
}
