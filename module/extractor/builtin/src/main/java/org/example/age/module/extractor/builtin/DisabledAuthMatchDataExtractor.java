package org.example.age.module.extractor.builtin;

import io.github.mikewacker.drift.api.HttpOptional;
import io.undertow.server.HttpServerExchange;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.api.def.AuthMatchData;
import org.example.age.api.extractor.AuthMatchDataExtractor;

/** {@link AuthMatchDataExtractor} that extracts {@link DisabledAuthMatchData}. */
@Singleton
final class DisabledAuthMatchDataExtractor implements AuthMatchDataExtractor {

    @Inject
    public DisabledAuthMatchDataExtractor() {}

    @Override
    public HttpOptional<AuthMatchData> tryExtract(HttpServerExchange exchange) {
        AuthMatchData authData = DisabledAuthMatchData.of();
        return HttpOptional.of(authData);
    }
}
