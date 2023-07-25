package org.example.age.testing;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

public final class TestKeyStoreTest {

    private static final byte[] MESSAGE = "Hello, world!".getBytes(StandardCharsets.UTF_8);

    @Test
    public void getSigningKeys() throws Exception {
        byte[] signature = TestSigning.signEd25519(
                MESSAGE, TestKeyStore.avsSigningKeyPair().getPrivate());
        boolean wasVerified = TestSigning.verifyEd25519(
                MESSAGE, signature, TestKeyStore.avsSigningKeyPair().getPublic());
        assertThat(wasVerified).isTrue();
    }

    @Test
    public void getIdKeys() {
        assertThat(TestKeyStore.remoteSiteIdKey()).isNotNull();
        assertThat(TestKeyStore.localSiteIdKey()).isNotNull();
    }
}
