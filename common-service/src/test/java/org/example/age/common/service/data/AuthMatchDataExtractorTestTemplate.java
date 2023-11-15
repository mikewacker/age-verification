package org.example.age.common.service.data;

import static org.assertj.core.api.Assertions.assertThat;

import io.undertow.server.HttpServerExchange;
import java.util.Optional;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.common.api.data.AuthMatchDataExtractor;
import org.example.age.data.crypto.Aes256Key;
import org.example.age.data.crypto.AesGcmEncryptionPackage;
import org.example.age.testing.api.FakeCodeSender;

/** Test template for {@link AuthMatchDataExtractor}. */
public final class AuthMatchDataExtractorTestTemplate {

    public static void match(
            AuthMatchDataExtractor extractor,
            Aes256Key key,
            HttpServerExchange localExchange,
            HttpServerExchange remoteExchange,
            boolean isMatchExpected) {
        AuthMatchData localData = extractAuthMatchData(extractor, localExchange);
        AuthMatchData remoteData = extractAuthMatchData(extractor, remoteExchange);
        AesGcmEncryptionPackage remoteToken = remoteData.encrypt(key);
        AuthMatchData decryptedRemoteData = decryptAuthMatchData(extractor, remoteToken, key);
        boolean isMatch = localData.match(decryptedRemoteData);
        assertThat(isMatch).isEqualTo(isMatchExpected);
    }

    private static AuthMatchData extractAuthMatchData(AuthMatchDataExtractor extractor, HttpServerExchange exchange) {
        FakeCodeSender sender = FakeCodeSender.create();
        Optional<AuthMatchData> maybeData = extractor.tryExtract(exchange, sender);
        return getAuthMatchData(maybeData, sender);
    }

    private static AuthMatchData decryptAuthMatchData(
            AuthMatchDataExtractor extractor, AesGcmEncryptionPackage token, Aes256Key key) {
        FakeCodeSender sender = FakeCodeSender.create();
        Optional<AuthMatchData> maybeData = extractor.tryDecrypt(token, key, sender);
        return getAuthMatchData(maybeData, sender);
    }

    private static AuthMatchData getAuthMatchData(Optional<AuthMatchData> maybeData, FakeCodeSender sender) {
        assertThat(maybeData).isPresent();
        assertThat(sender.tryGet()).isEmpty();
        return maybeData.get();
    }
}
