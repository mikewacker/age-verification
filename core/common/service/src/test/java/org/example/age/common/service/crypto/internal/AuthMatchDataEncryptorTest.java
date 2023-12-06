package org.example.age.common.service.crypto.internal;

import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import dagger.Component;
import javax.inject.Singleton;
import org.example.age.api.HttpOptional;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.data.crypto.Aes256Key;
import org.example.age.data.crypto.AesGcmEncryptionPackage;
import org.example.age.data.crypto.BytesValue;
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
        AuthMatchData authData = new TestAuthMatchData("test");
        AesGcmEncryptionPackage authToken = authDataEncryptor.encrypt(authData, authKey);
        HttpOptional<AuthMatchData> maybeRtAuthData = authDataEncryptor.tryDecrypt(authToken, authKey);
        assertThat(maybeRtAuthData).hasValue(authData);
    }

    @Test
    public void decryptFailed_DecryptionFailed() {
        AesGcmEncryptionPackage authToken = AesGcmEncryptionPackage.of(BytesValue.empty(), BytesValue.empty());
        decryptFailed(authToken, 401);
    }

    @Test
    public void decryptFailed_DeserializationFailed() {
        AesGcmEncryptionPackage authToken = AesGcmEncryptionPackage.encrypt(new byte[4], authKey);
        decryptFailed(authToken, 400);
    }

    private void decryptFailed(AesGcmEncryptionPackage authToken, int expectedErrorCode) {
        HttpOptional<AuthMatchData> maybeAuthData = authDataEncryptor.tryDecrypt(authToken, authKey);
        assertThat(maybeAuthData).isEmptyWithErrorCode(expectedErrorCode);
    }

    /** Test {@link AuthMatchData}. */
    public record TestAuthMatchData(String data) implements AuthMatchData {

        @Override
        public boolean match(AuthMatchData other) {
            return equals(other);
        }
    }

    /** Dagger component that provides a {@link AuthMatchDataEncryptor}. */
    @Component(modules = AuthMatchDataEncryptorModule.class)
    @Singleton
    interface TestComponent {

        static AuthMatchDataEncryptor createAuthMatchDataEncryptor() {
            TestComponent component = DaggerAuthMatchDataEncryptorTest_TestComponent.create();
            return component.authMatchDataEncryptor();
        }

        AuthMatchDataEncryptor authMatchDataEncryptor();
    }
}
