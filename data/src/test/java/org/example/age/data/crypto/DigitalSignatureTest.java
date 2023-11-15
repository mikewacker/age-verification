package org.example.age.data.crypto;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
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
        DigitalSignature signature = DigitalSignature.sign(MESSAGE, keyPair.getPrivate());
        byte[] otherMessage = "otherMessage".getBytes(StandardCharsets.UTF_8);
        boolean wasVerified = signature.verify(otherMessage, keyPair.getPublic());
        assertThat(wasVerified).isFalse();
    }

    @Test
    public void serializeThenDeserialize() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        DigitalSignature signature = DigitalSignature.sign(MESSAGE, keyPair.getPrivate());
        byte[] rawSignature = mapper.writeValueAsBytes(signature);
        DigitalSignature rtSignature = mapper.readValue(rawSignature, new TypeReference<>() {});
        assertThat(rtSignature).isEqualTo(signature);
    }

    @Test
    public void signThenSerializeThenDeserializeThenVerify() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        DigitalSignature signature = DigitalSignature.sign(MESSAGE, keyPair.getPrivate());
        byte[] rawSignature = mapper.writeValueAsBytes(signature);
        DigitalSignature rtSignature = mapper.readValue(rawSignature, new TypeReference<>() {});
        boolean wasVerified = rtSignature.verify(MESSAGE, keyPair.getPublic());
        assertThat(wasVerified).isTrue();
    }
}
