package org.example.age.module.extractor.common.builtin;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.def.common.AuthMatchData;
import org.example.age.api.module.extractor.common.AuthMatchDataExtractor;

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
