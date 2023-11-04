package org.example.age.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.KeyPair;
import org.example.age.data.SecureId;
import org.example.age.testing.crypto.TestSigning;
import org.junit.jupiter.api.Test;

public final class ResourcesTest {

    private static final byte[] MESSAGE = "Hello, world!".getBytes(StandardCharsets.UTF_8);

    @Test
    public void loadEd25519KeyPair() throws Exception {
        Path path = Path.of("keys", "CheckMyAge", "signing.pem");
        KeyPair keyPair = Resources.loadEd25519KeyPair(path);
        byte[] signature = TestSigning.signEd25519(MESSAGE, keyPair.getPrivate());
        boolean wasVerified = TestSigning.verifyEd25519(MESSAGE, signature, keyPair.getPublic());
        assertThat(wasVerified).isTrue();
    }

    @Test
    public void loadSecureId() throws IOException {
        Path path = Path.of("keys", "Crackle", "pseudonym.bin");
        SecureId key = Resources.loadSecureId(path);
        assertThat(key).isNotNull();
    }
}
