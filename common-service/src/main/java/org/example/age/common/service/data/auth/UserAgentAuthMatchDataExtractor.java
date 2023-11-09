package org.example.age.common.service.data.auth;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import javax.inject.Inject;
import org.example.age.common.api.Sender;
import org.example.age.common.api.data.auth.AuthMatchData;
import org.example.age.common.api.data.auth.AuthMatchDataExtractor;
import org.example.age.data.certificate.AuthKey;
import org.example.age.data.certificate.AuthToken;

/** Creates {@link UserAgentAuthMatchData}, or sends a 401 error if decryption fails. */
final class UserAgentAuthMatchDataExtractor implements AuthMatchDataExtractor {

    @Inject
    public UserAgentAuthMatchDataExtractor() {}

    @Override
    public Optional<AuthMatchData> tryExtract(HttpServerExchange exchange, Sender sender) {
        String userAgent = exchange.getRequestHeaders().getFirst(Headers.USER_AGENT);
        userAgent = (userAgent != null) ? userAgent : "";
        return Optional.of(new UserAgentAuthMatchData(userAgent));
    }

    @Override
    public Optional<AuthMatchData> tryDecrypt(AuthToken token, AuthKey key, Sender sender) {
        byte[] bytes;
        try {
            bytes = token.decrypt(key);
        } catch (RuntimeException e) {
            sender.sendError(StatusCodes.UNAUTHORIZED);
            return Optional.empty();
        }

        String userAgent = new String(bytes, StandardCharsets.UTF_8);
        return Optional.of(new UserAgentAuthMatchData(userAgent));
    }
}
