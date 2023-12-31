package org.example.age.data.crypto;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.mikewacker.drift.testing.json.JsonTester;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class DigitalSignatureTest {

    private static final byte[] MESSAGE = "Hello, world!".getBytes(StandardCharsets.UTF_8);

    private static KeyPair keyPair;

    @BeforeAll
    public static void generateKeys() {
        keyPair = SigningKeys.generateEd25519KeyPair();
    }

    @Test
    public void signThenVerify() {
        DigitalSignature signature = DigitalSignature.sign(MESSAGE, keyPair.getPrivate());
        boolean wasVerified = signature.verify(MESSAGE, keyPair.getPublic());
        assertThat(wasVerified).isTrue();
    }

    @Test
    public void verifyFailed() {
        DigitalSignature forgedSignature = DigitalSignature.ofBytes(new byte[32]);
        boolean wasVerified = forgedSignature.verify(MESSAGE, keyPair.getPublic());
        assertThat(wasVerified).isFalse();
    }

    @Test
    public void serializeThenDeserialize() {
        JsonTester.serializeThenDeserialize(
                DigitalSignature.sign(MESSAGE, keyPair.getPrivate()), new TypeReference<>() {});
    }
}
