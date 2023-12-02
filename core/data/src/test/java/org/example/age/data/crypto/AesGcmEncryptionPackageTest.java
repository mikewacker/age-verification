package org.example.age.data.crypto;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
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
        AesGcmEncryptionPackage otherEncryptionPackage =
                AesGcmEncryptionPackage.of(encryptionPackage.ciphertext(), BytesValue.ofBytes(new byte[12]));
        Optional<byte[]> maybeRtPlaintext = otherEncryptionPackage.tryDecrypt(key);
        assertThat(maybeRtPlaintext).isEmpty();
    }

    @Test
    public void serializeThenDeserialize() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        AesGcmEncryptionPackage encryptionPackage = AesGcmEncryptionPackage.encrypt(PLAINTEXT, key);
        byte[] rawEncryptionPackage = mapper.writeValueAsBytes(encryptionPackage);
        AesGcmEncryptionPackage rtEncryptionPackage = mapper.readValue(rawEncryptionPackage, new TypeReference<>() {});
        assertThat(rtEncryptionPackage).isEqualTo(encryptionPackage);
    }

    @Test
    public void encryptThenSerializeThenDeserializeThenDecrypt() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        AesGcmEncryptionPackage encryptionPackage = AesGcmEncryptionPackage.encrypt(PLAINTEXT, key);
        byte[] rawEncryptionPackage = mapper.writeValueAsBytes(encryptionPackage);
        AesGcmEncryptionPackage rtEncryptionPackage = mapper.readValue(rawEncryptionPackage, new TypeReference<>() {});
        Optional<byte[]> maybeRtPlaintext = rtEncryptionPackage.tryDecrypt(key);
        assertThat(maybeRtPlaintext).hasValue(PLAINTEXT);
    }
}
