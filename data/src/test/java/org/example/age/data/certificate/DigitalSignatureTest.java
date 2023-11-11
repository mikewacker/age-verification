package org.example.age.data.certificate;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.security.KeyPair;
import org.example.age.data.DataMapper;
import org.example.age.testing.crypto.TestKeys;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class DigitalSignatureTest {

    private static final String MESSAGE = "Hello, world!";

    private static KeyPair keyPair;

    @BeforeAll
    public static void generateKeys() {
        keyPair = TestKeys.generateEd25519KeyPair();
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
        boolean wasVerified = signature.verify("other message", keyPair.getPublic());
        assertThat(wasVerified).isFalse();
    }

    @Test
    public void serializeThenDeserialize() throws IOException {
        DigitalSignature signature = DigitalSignature.sign(MESSAGE, keyPair.getPrivate());
        byte[] rawSignature = DataMapper.get().writeValueAsBytes(signature);
        DigitalSignature rtSignature = DataMapper.get().readValue(rawSignature, new TypeReference<>() {});
        assertThat(rtSignature).isEqualTo(signature);
    }

    @Test
    public void signThenSerializeThenDeserializeThenVerify() throws IOException {
        DigitalSignature signature = DigitalSignature.sign(MESSAGE, keyPair.getPrivate());
        byte[] rawSignature = DataMapper.get().writeValueAsBytes(signature);
        DigitalSignature rtSignature = DataMapper.get().readValue(rawSignature, new TypeReference<>() {});
        boolean wasVerified = rtSignature.verify(MESSAGE, keyPair.getPublic());
        assertThat(wasVerified).isTrue();
    }
}
