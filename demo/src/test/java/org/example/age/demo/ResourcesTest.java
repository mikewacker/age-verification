package org.example.age.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Path;
import java.security.KeyPair;
import org.example.age.data.crypto.SecureId;
import org.junit.jupiter.api.Test;

public final class ResourcesTest {

    @Test
    public void loadEd25519KeyPair() throws Exception {
        Path path = Path.of("keys", "CheckMyAge", "signing.pem");
        KeyPair keyPair = Resources.loadEd25519KeyPair(path);
        assertThat(keyPair).isNotNull();
    }

    @Test
    public void loadSecureId() throws IOException {
        Path path = Path.of("keys", "Crackle", "pseudonym.bin");
        SecureId key = Resources.loadSecureId(path);
        assertThat(key).isNotNull();
    }
}
