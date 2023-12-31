package org.example.age.service.crypto.internal;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.mikewacker.drift.api.HttpOptional;
import io.github.mikewacker.drift.json.JsonValues;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.api.def.AuthMatchData;
import org.example.age.data.crypto.Aes256Key;
import org.example.age.data.crypto.AesGcmEncryptionPackage;

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

        return JsonValues.tryDeserialize(rawAuthData, new TypeReference<>() {}, 400);
    }
}
