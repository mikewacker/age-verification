package org.example.age.common.service.data;

import io.undertow.server.HttpServerExchange;
import java.util.Optional;
import javax.inject.Inject;
import org.example.age.api.Sender;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.common.api.data.AuthMatchDataExtractor;
import org.example.age.data.crypto.Aes256Key;
import org.example.age.data.crypto.AesGcmEncryptionPackage;

/** Creates {@link DisabledAuthMatchData}. */
final class DisabledAuthMatchDataExtractor implements AuthMatchDataExtractor {

    @Inject
    public DisabledAuthMatchDataExtractor() {}

    @Override
    public Optional<AuthMatchData> tryExtract(HttpServerExchange exchange, Sender sender) {
        return Optional.of(new DisabledAuthMatchData());
    }

    @Override
    public Optional<AuthMatchData> tryDecrypt(AesGcmEncryptionPackage token, Aes256Key key, Sender sender) {
        return Optional.of(new DisabledAuthMatchData());
    }
}
