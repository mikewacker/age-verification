package org.example.age.module.extractor.builtin;

import io.github.mikewacker.drift.api.HttpOptional;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.example.age.api.def.AuthMatchData;
import org.example.age.api.extractor.AuthMatchDataExtractor;

/** {@link AuthMatchDataExtractor} that extracts {@link UserAgentAuthMatchData}. */
@Singleton
final class UserAgentAuthMatchDataExtractor implements AuthMatchDataExtractor {

    @Inject
    public UserAgentAuthMatchDataExtractor() {}

    @Override
    public HttpOptional<AuthMatchData> tryExtract(HttpServerExchange exchange) {
        String userAgent = exchange.getRequestHeaders().getFirst(Headers.USER_AGENT);
        userAgent = (userAgent != null) ? userAgent : "";
        AuthMatchData authData = UserAgentAuthMatchData.of(userAgent);
        return HttpOptional.of(authData);
    }
}
