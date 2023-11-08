package org.example.age.common.service.data.auth;

import io.undertow.server.HttpServerExchange;
import javax.inject.Inject;
import org.example.age.common.api.data.auth.AuthMatchData;
import org.example.age.common.api.data.auth.AuthMatchDataExtractor;
import org.example.age.data.certificate.AuthKey;
import org.example.age.data.certificate.AuthToken;

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