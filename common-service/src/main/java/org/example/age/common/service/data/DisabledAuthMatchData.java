package org.example.age.common.service.data;

import org.example.age.common.api.data.AuthMatchData;
import org.example.age.data.crypto.Aes256Key;
import org.example.age.data.crypto.AesGcmEncryptionPackage;
import org.example.age.data.crypto.BytesValue;

/** Always returns a successful match. */
final class DisabledAuthMatchData implements AuthMatchData {

    @Override
    public boolean match(AuthMatchData other) {
        return true;
    }

    @Override
    public AesGcmEncryptionPackage encrypt(Aes256Key key) {
        return AesGcmEncryptionPackage.of(BytesValue.ofBytes(new byte[1]), BytesValue.ofBytes(new byte[1]));
    }
}
