package org.example.age.common.service.data;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import javax.inject.Inject;
import org.example.age.api.Sender;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.common.api.data.AuthMatchDataExtractor;
import org.example.age.data.crypto.Aes256Key;
import org.example.age.data.crypto.AesGcmEncryptionPackage;

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
    public Optional<AuthMatchData> tryDecrypt(AesGcmEncryptionPackage token, Aes256Key key, Sender sender) {
        Optional<byte[]> maybeBytes = token.tryDecrypt(key);
        if (maybeBytes.isEmpty()) {
            sender.sendError(StatusCodes.UNAUTHORIZED);
            return Optional.empty();
        }
        byte[] bytes = maybeBytes.get();

        String userAgent = new String(bytes, StandardCharsets.UTF_8);
        return Optional.of(new UserAgentAuthMatchData(userAgent));
    }
}
