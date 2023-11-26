package org.example.age.common.service.crypto.internal;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.api.HttpOptional;
import org.example.age.api.JsonSerializer;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.data.crypto.Aes256Key;
import org.example.age.data.crypto.AesGcmEncryptionPackage;

@Singleton
final class AuthMatchDataEncryptorImpl implements AuthMatchDataEncryptor {

    private final JsonSerializer serializer;

    @Inject
    public AuthMatchDataEncryptorImpl(JsonSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public AesGcmEncryptionPackage encrypt(AuthMatchData authData, Aes256Key authKey) {
        byte[] rawAuthData = serializer.serialize(authData);
        return AesGcmEncryptionPackage.encrypt(rawAuthData, authKey);
    }

    @Override
    public HttpOptional<AuthMatchData> tryDecrypt(AesGcmEncryptionPackage authToken, Aes256Key authKey) {
        Optional<byte[]> maybeRawAuthData = authToken.tryDecrypt(authKey);
        if (maybeRawAuthData.isEmpty()) {
            return HttpOptional.empty(401);
        }
        byte[] rawAuthData = maybeRawAuthData.get();

        return serializer.tryDeserialize(rawAuthData, new TypeReference<>() {}, 400);
    }
}
