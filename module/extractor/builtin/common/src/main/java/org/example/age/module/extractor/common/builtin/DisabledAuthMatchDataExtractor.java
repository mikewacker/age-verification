package org.example.age.module.extractor.common.builtin;

import io.undertow.server.HttpServerExchange;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.def.common.AuthMatchData;
import org.example.age.api.extractor.common.AuthMatchDataExtractor;

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
