package org.example.age.data.crypto;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class EncryptionUtilsTest {

    private static final byte[] PLAINTEXT = "Hello, world!".getBytes(StandardCharsets.UTF_8);

    private static byte[] key;
    private static byte[] otherKey;

    @BeforeAll
    public static void generateKeys() {
        key = SecureRandomUtils.generateBytes(32);
        otherKey = SecureRandomUtils.generateBytes(32);
    }

    @Test
    public void encryptThenDecrypt() {
        byte[] iv = EncryptionUtils.createIv();
        byte[] ciphertext = EncryptionUtils.encrypt(PLAINTEXT, key, iv);
        Optional<byte[]> maybeRtPlaintext = EncryptionUtils.tryDecrypt(ciphertext, key, iv);
        assertThat(maybeRtPlaintext).hasValue(PLAINTEXT);
    }

    @Test
    public void decryptFailed_TamperedCiphertext() {
        byte[] iv = EncryptionUtils.createIv();
        byte[] ciphertext = EncryptionUtils.encrypt(PLAINTEXT, key, iv);
        byte[] tamperedCiphertext = tamper(ciphertext);
        Optional<byte[]> maybeRtPlaintext = EncryptionUtils.tryDecrypt(tamperedCiphertext, key, iv);
        assertThat(maybeRtPlaintext).isEmpty();
    }

    @Test
    public void decryptFailed_TamperedIv() {
        byte[] iv = EncryptionUtils.createIv();
        byte[] ciphertext = EncryptionUtils.encrypt(PLAINTEXT, key, iv);
        byte[] tamperedIv = tamper(iv);
        Optional<byte[]> maybeRtPlaintext = EncryptionUtils.tryDecrypt(ciphertext, key, tamperedIv);
        assertThat(maybeRtPlaintext).isEmpty();
    }

    @Test
    public void decryptFailed_WrongKey() {
        byte[] iv = EncryptionUtils.createIv();
        byte[] ciphertext = EncryptionUtils.encrypt(PLAINTEXT, key, iv);
        Optional<byte[]> maybeRtPlaintext = EncryptionUtils.tryDecrypt(ciphertext, otherKey, iv);
        assertThat(maybeRtPlaintext).isEmpty();
    }

    private static byte[] tamper(byte[] bytes) {
        byte[] tamperedBytes = Arrays.copyOf(bytes, bytes.length);
        Arrays.fill(tamperedBytes, 0, 4, (byte) 0);
        return tamperedBytes;
    }
}
