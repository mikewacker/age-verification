package org.example.age.common.service.data.auth;

import io.undertow.server.HttpServerExchange;
import java.util.Optional;
import javax.inject.Inject;
import org.example.age.common.api.Sender;
import org.example.age.common.api.data.auth.AuthMatchData;
import org.example.age.common.api.data.auth.AuthMatchDataExtractor;
import org.example.age.data.certificate.AuthKey;
import org.example.age.data.certificate.AuthToken;

/** Creates {@link DisabledAuthMatchData}. */
final class DisabledAuthMatchDataExtractor implements AuthMatchDataExtractor {

    @Inject
    public DisabledAuthMatchDataExtractor() {}

    @Override
    public Optional<AuthMatchData> tryExtract(HttpServerExchange exchange, Sender sender) {
        return Optional.of(new DisabledAuthMatchData());
    }

    @Override
    public Optional<AuthMatchData> tryDecrypt(AuthToken token, AuthKey key, Sender sender) {
        return Optional.of(new DisabledAuthMatchData());
    }
}
