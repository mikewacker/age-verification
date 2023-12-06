package org.example.age.data.crypto;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import org.example.age.api.JsonSerializer;
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
        DigitalSignature signature = DigitalSignature.sign(MESSAGE, keyPair.getPrivate());
        byte[] otherMessage = "Goodbye, world!".getBytes(StandardCharsets.UTF_8);
        boolean wasVerified = signature.verify(otherMessage, keyPair.getPublic());
        assertThat(wasVerified).isFalse();
    }

    @Test
    public void serializeThenDeserialize() {
        DigitalSignature signature = DigitalSignature.sign(MESSAGE, keyPair.getPrivate());
        byte[] rawSignature = JsonSerializer.serialize(signature);
        DigitalSignature rtSignature = JsonSerializer.deserialize(rawSignature, new TypeReference<>() {});
        assertThat(rtSignature).isEqualTo(signature);
    }

    @Test
    public void signThenSerializeThenDeserializeThenVerify() {
        DigitalSignature signature = DigitalSignature.sign(MESSAGE, keyPair.getPrivate());
        byte[] rawSignature = JsonSerializer.serialize(signature);
        DigitalSignature rtSignature = JsonSerializer.deserialize(rawSignature, new TypeReference<>() {});
        boolean wasVerified = rtSignature.verify(MESSAGE, keyPair.getPublic());
        assertThat(wasVerified).isTrue();
    }
}
