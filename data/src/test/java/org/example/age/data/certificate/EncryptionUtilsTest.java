package org.example.age.data.certificate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.assertj.core.api.ThrowableAssert;
import org.example.age.testing.crypto.TestKeys;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class EncryptionUtilsTest {

    private static final byte[] PLAINTEXT = "Hello, world!".getBytes(StandardCharsets.UTF_8);

    private static SecretKey key;
    private static SecretKey otherKey;
    private static SecretKey invalidKey;

    @BeforeAll
    public static void generateKeys() {
        key = TestKeys.generateAes256Key();
        otherKey = TestKeys.generateAes256Key();
        invalidKey = generateDesKey();
    }

    @Test
    public void encryptThenDecrypt() {
        byte[] encryptionPackage = EncryptionUtils.encrypt(PLAINTEXT, key);
        byte[] decryptedPlaintext = EncryptionUtils.decrypt(encryptionPackage, key);
        assertThat(decryptedPlaintext).isEqualTo(PLAINTEXT);
    }

    @Test
    public void error_Decrypt_TamperedCiphertext() {
        byte[] encryptionPackage = EncryptionUtils.encrypt(PLAINTEXT, key);
        tamperCiphertext(encryptionPackage);
        error_Decrypt(encryptionPackage, "decryption failed");
    }

    @Test
    public void error_Decrypt_TamperedIv() {
        byte[] encryptionPackage = EncryptionUtils.encrypt(PLAINTEXT, key);
        tamperIv(encryptionPackage);
        error_Decrypt(encryptionPackage, "decryption failed");
    }

    @Test
    public void error_Decrypt_WrongKey() {
        byte[] encryptionPackage = EncryptionUtils.encrypt(PLAINTEXT, otherKey);
        error_Decrypt(encryptionPackage, "decryption failed");
    }

    @Test
    public void error_Decrypt_TooShort() {
        byte[] encryptionPackage = new byte[4];
        error_Decrypt(encryptionPackage, "encryption package is too short");
    }

    private void error_Decrypt(byte[] encryptionPackage, String expectedMessage) {
        assertThatThrownBy(() -> EncryptionUtils.decrypt(encryptionPackage, key))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);
    }

    @Test
    public void error_InvalidKey_Encrypt() {
        error_InvalidKey(() -> EncryptionUtils.encrypt(PLAINTEXT, invalidKey));
    }

    @Test
    public void error_InvalidKey_Decrypt() {
        byte[] encryptionPackage = new byte[1024];
        error_InvalidKey(() -> EncryptionUtils.decrypt(encryptionPackage, invalidKey));
    }

    private void error_InvalidKey(ThrowableAssert.ThrowingCallable callable) {
        assertThatThrownBy(callable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("key must be a valid AES key");
    }

    private static void tamperCiphertext(byte[] encryptionPackage) {
        Arrays.fill(encryptionPackage, 12, 16, (byte) 0);
    }

    private static void tamperIv(byte[] encryptionPackage) {
        Arrays.fill(encryptionPackage, 0, 4, (byte) 0);
    }

    private static SecretKey generateDesKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
