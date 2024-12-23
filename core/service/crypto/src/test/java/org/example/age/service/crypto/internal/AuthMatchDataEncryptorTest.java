package org.example.age.service.crypto.internal;

import static io.github.mikewacker.drift.testing.api.Assertions.assertThat;

import dagger.Component;
import io.github.mikewacker.drift.api.HttpOptional;
import jakarta.inject.Singleton;
import org.example.age.api.def.AuthMatchData;
import org.example.age.data.crypto.Aes256Key;
import org.example.age.data.crypto.AesGcmEncryptionPackage;
import org.example.age.module.extractor.builtin.UserAgentAuthMatchData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class AuthMatchDataEncryptorTest {

    private static AuthMatchDataEncryptor authDataEncryptor;

    private static Aes256Key authKey;

    @BeforeAll
    public static void createAuthMatchDataEncryptor() {
        authDataEncryptor = TestComponent.createAuthMatchDataEncryptor();
    }

    @BeforeAll
    public static void generateKeys() {
        authKey = Aes256Key.generate();
    }

    @Test
    public void encryptThenDecrypt() {
        AuthMatchData authData = UserAgentAuthMatchData.of("agent");
        AesGcmEncryptionPackage authToken = authDataEncryptor.encrypt(authData, authKey);
        HttpOptional<AuthMatchData> maybeRtAuthData = authDataEncryptor.tryDecrypt(authToken, authKey);
        assertThat(maybeRtAuthData).hasValue(authData);
    }

    @Test
    public void decryptFailed_DecryptionFailed() {
        AesGcmEncryptionPackage invalidAuthToken = AesGcmEncryptionPackage.empty();
        HttpOptional<AuthMatchData> maybeAuthData = authDataEncryptor.tryDecrypt(invalidAuthToken, authKey);
        assertThat(maybeAuthData).isEmptyWithErrorCode(401);
    }

    @Test
    public void decryptFailed_DeserializationFailed() {
        byte[] malformedRawAuthData = new byte[4];
        AesGcmEncryptionPackage authToken = AesGcmEncryptionPackage.encrypt(malformedRawAuthData, authKey);
        HttpOptional<AuthMatchData> maybeAuthData = authDataEncryptor.tryDecrypt(authToken, authKey);
        assertThat(maybeAuthData).isEmptyWithErrorCode(400);
    }

    /** Dagger component that provides a {@link AuthMatchDataEncryptor}. */
    @Component(modules = VerifierCryptoModule.class)
    @Singleton
    interface TestComponent {

        static AuthMatchDataEncryptor createAuthMatchDataEncryptor() {
            TestComponent component = DaggerAuthMatchDataEncryptorTest_TestComponent.create();
            return component.authMatchDataEncryptor();
        }

        AuthMatchDataEncryptor authMatchDataEncryptor();
    }
}
