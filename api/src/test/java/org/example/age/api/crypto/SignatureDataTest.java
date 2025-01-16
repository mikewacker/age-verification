package org.example.age.api.crypto;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class SignatureDataTest {

    private static ObjectMapper mapper;
    private static KeyPair keyPair;

    private Signature signer;
    private Signature verifier;

    @BeforeAll
    public static void createObjectMapperAndKeyPair() throws Exception {
        mapper = new ObjectMapper();
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
        AlgorithmParameterSpec nistP256Spec = new ECGenParameterSpec("secp256r1");
        keyPairGenerator.initialize(nistP256Spec);
        keyPair = keyPairGenerator.generateKeyPair();
    }

    @BeforeEach
    public void createSignerAndVerifier() throws Exception {
        signer = Signature.getInstance("SHA256withECDSA");
        signer.initSign(keyPair.getPrivate());
        verifier = Signature.getInstance("SHA256withECDSA");
        verifier.initVerify(keyPair.getPublic());
    }

    @Test
    public void signThenSerializeThenDeserializeThenVerify() throws IOException {
        String text = "Hello, world!";
        SignatureData signature = SignatureData.sign(text, mapper, signer);
        String json = mapper.writeValueAsString(signature);
        SignatureData rtSignature = mapper.readValue(json, SignatureData.class);
        rtSignature.verify(text, mapper, verifier);
    }

    @Test
    public void failToVerify() {
        SignatureData signature = SignatureData.fromString("AAAAAA");
        assertThatThrownBy(() -> signature.verify("test", mapper, verifier))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("signature verification failed");
    }
}
