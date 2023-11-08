package org.example.age.common.service.data.auth;

import org.example.age.common.api.data.auth.AuthMatchData;
import org.example.age.data.certificate.AuthKey;
import org.example.age.data.certificate.AuthToken;

/** Always returns a successful match. */
final class DisabledAuthMatchData implements AuthMatchData {

    @Override
    public boolean match(AuthMatchData other) {
        return true;
    }

    @Override
    public AuthToken encrypt(AuthKey key) {
        return AuthToken.empty();
    }
}
