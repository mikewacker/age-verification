package org.example.age.common.service.data;

import java.nio.charset.StandardCharsets;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.data.crypto.Aes256Key;
import org.example.age.data.crypto.AesGcmEncryptionPackage;

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
    public AesGcmEncryptionPackage encrypt(Aes256Key key) {
        byte[] bytes = userAgent.getBytes(StandardCharsets.UTF_8);
        return AesGcmEncryptionPackage.encrypt(bytes, key);
    }
}
