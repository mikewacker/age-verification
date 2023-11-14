package org.example.age.common.service.data;

import org.example.age.common.api.data.AuthMatchData;
import org.example.age.data.crypto.AuthKey;
import org.example.age.data.crypto.AuthToken;

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
