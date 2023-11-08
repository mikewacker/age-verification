package org.example.age.common.service.data.auth;

import static org.assertj.core.api.Assertions.assertThat;

import io.undertow.server.HttpServerExchange;
import org.example.age.common.api.data.auth.AuthMatchData;
import org.example.age.common.api.data.auth.AuthMatchDataExtractor;
import org.example.age.data.certificate.AuthKey;
import org.example.age.data.certificate.AuthToken;

/** Test template for {@link AuthMatchDataExtractor}. */
public final class AuthMatchDataExtractorTestTemplate {

    public static void match(
            AuthMatchDataExtractor extractor,
            AuthKey key,
            HttpServerExchange localExchange,
            HttpServerExchange remoteExchange,
            boolean isMatchExpected) {
        AuthMatchData localData = extractor.extract(localExchange);
        AuthMatchData remoteData = extractor.extract(remoteExchange);
        AuthToken remoteToken = remoteData.encrypt(key);
        AuthMatchData decryptedRemoteData = extractor.decrypt(remoteToken, key);
        boolean isMatch = localData.match(decryptedRemoteData);
        assertThat(isMatch).isEqualTo(isMatchExpected);
    }
}