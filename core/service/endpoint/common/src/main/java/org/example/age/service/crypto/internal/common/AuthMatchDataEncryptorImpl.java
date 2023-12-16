package org.example.age.service.crypto.internal.common;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.def.common.AuthMatchData;
import org.example.age.data.crypto.Aes256Key;
import org.example.age.data.crypto.AesGcmEncryptionPackage;
import org.example.age.data.json.JsonValues;

@Singleton
final class AuthMatchDataEncryptorImpl implements AuthMatchDataEncryptor {

    @Inject
    public AuthMatchDataEncryptorImpl() {}

    @Override
    public AesGcmEncryptionPackage encrypt(AuthMatchData authData, Aes256Key authKey) {
        byte[] rawAuthData = JsonValues.serialize(authData);
        return AesGcmEncryptionPackage.encrypt(rawAuthData, authKey);
    }

    @Override
    public HttpOptional<AuthMatchData> tryDecrypt(AesGcmEncryptionPackage authToken, Aes256Key authKey) {
        Optional<byte[]> maybeRawAuthData = authToken.tryDecrypt(authKey);
        if (maybeRawAuthData.isEmpty()) {
            return HttpOptional.empty(401);
        }
        byte[] rawAuthData = maybeRawAuthData.get();

        Optional<AuthMatchData> maybeAuthData = JsonValues.tryDeserialize(rawAuthData, new TypeReference<>() {});
        return HttpOptional.fromOptional(maybeAuthData, 400);
    }
}
