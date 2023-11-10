package org.example.age.common.service.data;

import static org.assertj.core.api.Assertions.assertThat;

import io.undertow.server.HttpServerExchange;
import java.util.Optional;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.common.api.data.AuthMatchDataExtractor;
import org.example.age.data.certificate.AuthKey;
import org.example.age.data.certificate.AuthToken;
import org.example.age.testing.service.TestCodeSender;

/** Test template for {@link AuthMatchDataExtractor}. */
public final class AuthMatchDataExtractorTestTemplate {

    public static void match(
            AuthMatchDataExtractor extractor,
            AuthKey key,
            HttpServerExchange localExchange,
            HttpServerExchange remoteExchange,
            boolean isMatchExpected) {
        AuthMatchData localData = extractAuthMatchData(extractor, localExchange);
        AuthMatchData remoteData = extractAuthMatchData(extractor, remoteExchange);
        AuthToken remoteToken = remoteData.encrypt(key);
        AuthMatchData decryptedRemoteData = decryptAuthMatchData(extractor, remoteToken, key);
        boolean isMatch = localData.match(decryptedRemoteData);
        assertThat(isMatch).isEqualTo(isMatchExpected);
    }

    private static AuthMatchData extractAuthMatchData(AuthMatchDataExtractor extractor, HttpServerExchange exchange) {
        TestCodeSender sender = TestCodeSender.create();
        Optional<AuthMatchData> maybeData = extractor.tryExtract(exchange, sender);
        return getAuthMatchData(maybeData, sender);
    }

    private static AuthMatchData decryptAuthMatchData(AuthMatchDataExtractor extractor, AuthToken token, AuthKey key) {
        TestCodeSender sender = TestCodeSender.create();
        Optional<AuthMatchData> maybeData = extractor.tryDecrypt(token, key, sender);
        return getAuthMatchData(maybeData, sender);
    }

    private static AuthMatchData getAuthMatchData(Optional<AuthMatchData> maybeData, TestCodeSender sender) {
        assertThat(maybeData).isPresent();
        assertThat(sender.wasSent()).isFalse();
        return maybeData.get();
    }
}
