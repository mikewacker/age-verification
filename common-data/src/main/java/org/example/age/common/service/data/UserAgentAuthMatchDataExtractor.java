package org.example.age.common.service.data;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.api.HttpOptional;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.common.api.data.AuthMatchDataExtractor;

/** {@link AuthMatchDataExtractor} that extracts {@link UserAgentAuthMatchData}. */
@Singleton
final class UserAgentAuthMatchDataExtractor implements AuthMatchDataExtractor {

    @Inject
    public UserAgentAuthMatchDataExtractor() {}

    @Override
    public HttpOptional<AuthMatchData> tryExtract(HttpServerExchange exchange) {
        String userAgent = exchange.getRequestHeaders().getFirst(Headers.USER_AGENT);
        userAgent = (userAgent != null) ? userAgent : "";
        AuthMatchData data = UserAgentAuthMatchData.of(userAgent);
        return HttpOptional.of(data);
    }
}