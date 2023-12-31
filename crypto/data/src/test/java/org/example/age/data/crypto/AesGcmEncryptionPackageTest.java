package org.example.age.data.crypto;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.mikewacker.drift.testing.json.JsonTester;
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
        AesGcmEncryptionPackage invalidEncryptionPackage = AesGcmEncryptionPackage.empty();
        Optional<byte[]> maybeRtPlaintext = invalidEncryptionPackage.tryDecrypt(key);
        assertThat(maybeRtPlaintext).isEmpty();
    }

    @Test
    public void serializeThenDeserialize() {
        JsonTester.serializeThenDeserialize(AesGcmEncryptionPackage.encrypt(PLAINTEXT, key), new TypeReference<>() {});
    }
}
