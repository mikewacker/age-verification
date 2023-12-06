package org.example.age.data.crypto;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.example.age.api.JsonSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class AesGcmEncryptionPackageTest {

    private static final byte[] PLAINTEXT = "Hello, world!".getBytes(StandardCharsets.UTF_8);

    private static Aes256Key key;

    @BeforeAll
    public static void generateKeys() {
        key = Aes256Key.generate();
    }

    @Test
    public void encryptThenDecrypt() {
        AesGcmEncryptionPackage encryptionPackage = AesGcmEncryptionPackage.encrypt(PLAINTEXT, key);
        Optional<byte[]> maybeRtPlaintext = encryptionPackage.tryDecrypt(key);
        assertThat(maybeRtPlaintext).hasValue(PLAINTEXT);
    }

    @Test
    public void decryptFailed() {
        AesGcmEncryptionPackage encryptionPackage = AesGcmEncryptionPackage.encrypt(PLAINTEXT, key);
        BytesValue otherIv = BytesValue.ofBytes(new byte[12]);
        AesGcmEncryptionPackage otherEncryptionPackage =
                AesGcmEncryptionPackage.of(encryptionPackage.ciphertext(), otherIv);
        Optional<byte[]> maybeRtPlaintext = otherEncryptionPackage.tryDecrypt(key);
        assertThat(maybeRtPlaintext).isEmpty();
    }

    @Test
    public void serializeThenDeserialize() {
        AesGcmEncryptionPackage encryptionPackage = AesGcmEncryptionPackage.encrypt(PLAINTEXT, key);
        byte[] rawEncryptionPackage = JsonSerializer.serialize(encryptionPackage);
        AesGcmEncryptionPackage rtEncryptionPackage =
                JsonSerializer.deserialize(rawEncryptionPackage, new TypeReference<>() {});
        assertThat(rtEncryptionPackage).isEqualTo(encryptionPackage);
    }

    @Test
    public void encryptThenSerializeThenDeserializeThenDecrypt() {
        AesGcmEncryptionPackage encryptionPackage = AesGcmEncryptionPackage.encrypt(PLAINTEXT, key);
        byte[] rawEncryptionPackage = JsonSerializer.serialize(encryptionPackage);
        AesGcmEncryptionPackage rtEncryptionPackage =
                JsonSerializer.deserialize(rawEncryptionPackage, new TypeReference<>() {});
        Optional<byte[]> maybeRtPlaintext = rtEncryptionPackage.tryDecrypt(key);
        assertThat(maybeRtPlaintext).hasValue(PLAINTEXT);
    }
}
