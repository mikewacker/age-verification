package org.example.age.testing.crypto;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.Test;

public final class TestKeysTest {

    private static final byte[] MESSAGE = "Hello, world!".getBytes(StandardCharsets.UTF_8);

    @Test
    public void generateEd25519KeyPair() throws Exception {
        KeyPair keyPair = TestKeys.generateEd25519KeyPair();
        byte[] signature = TestSigning.signEd25519(MESSAGE, keyPair.getPrivate());
        boolean wasVerified = TestSigning.verifyEd25519(MESSAGE, signature, keyPair.getPublic());
        assertThat(wasVerified).isTrue();
    }

    @Test
    public void generateAes256Key() throws Exception {
        SecretKey key = TestKeys.generateAes256Key();
        byte[] iv = TestEncrypting.createGcmIv();
        byte[] ciphertext = TestEncrypting.encryptAesGcm(MESSAGE, key, iv);
        byte[] plaintext = TestEncrypting.decryptAesGcm(ciphertext, key, iv);
        assertThat(plaintext).isEqualTo(MESSAGE);
    }
}
