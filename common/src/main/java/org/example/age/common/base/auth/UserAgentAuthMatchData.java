package org.example.age.common.base.auth;

import java.nio.charset.StandardCharsets;
import org.example.age.common.api.data.auth.AuthMatchData;
import org.example.age.data.certificate.AuthKey;
import org.example.age.data.certificate.AuthToken;

/** Matches the {@code User-Agent} header. */
final class UserAgentAuthMatchData implements AuthMatchData {

    private final String userAgent;

    public UserAgentAuthMatchData(String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public boolean match(AuthMatchData other) {
        UserAgentAuthMatchData otherData =
                (other instanceof UserAgentAuthMatchData) ? (UserAgentAuthMatchData) other : null;
        if (otherData == null) {
            return false;
        }

        return userAgent.equals(otherData.userAgent);
    }

    @Override
    public AuthToken encrypt(AuthKey key) {
        byte[] bytes = userAgent.getBytes(StandardCharsets.UTF_8);
        return AuthToken.encrypt(bytes, key);
    }
}
