package org.example.age.common.verification.auth;

import org.example.age.certificate.AuthKey;
import org.example.age.certificate.AuthToken;

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
