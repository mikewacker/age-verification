package org.example.age.common.verification.auth;

import io.undertow.server.HttpServerExchange;
import javax.inject.Inject;
import org.example.age.certificate.AuthKey;
import org.example.age.certificate.AuthToken;

/** Creates {@link DisabledAuthMatchData}. */
final class DisabledAuthMatchDataExtractor implements AuthMatchDataExtractor {

    @Inject
    public DisabledAuthMatchDataExtractor() {}

    @Override
    public AuthMatchData extract(HttpServerExchange exchange) {
        return new DisabledAuthMatchData();
    }

    @Override
    public AuthMatchData decrypt(AuthToken token, AuthKey key) {
        return new DisabledAuthMatchData();
    }
}