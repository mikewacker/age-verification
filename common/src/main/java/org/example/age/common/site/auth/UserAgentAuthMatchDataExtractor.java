package org.example.age.common.site.auth;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import java.nio.charset.StandardCharsets;
import javax.inject.Inject;
import org.example.age.data.certificate.AuthKey;
import org.example.age.data.certificate.AuthToken;

/** Creates {@link UserAgentAuthMatchData}. */
final class UserAgentAuthMatchDataExtractor implements AuthMatchDataExtractor {

    @Inject
    public UserAgentAuthMatchDataExtractor() {}

    @Override
    public AuthMatchData extract(HttpServerExchange exchange) {
        String userAgent = exchange.getRequestHeaders().getFirst(Headers.USER_AGENT);
        userAgent = (userAgent != null) ? userAgent : "";
        return new UserAgentAuthMatchData(userAgent);
    }

    @Override
    public AuthMatchData decrypt(AuthToken token, AuthKey key) {
        byte[] bytes = token.decrypt(key);
        String userAgent = new String(bytes, StandardCharsets.UTF_8);
        return new UserAgentAuthMatchData(userAgent);
    }
}
