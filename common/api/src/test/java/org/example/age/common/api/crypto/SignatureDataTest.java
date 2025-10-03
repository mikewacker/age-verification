package org.example.age.common.api.crypto;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class SignatureDataTest {

    private static KeyPair keyPair;

    private Signature signer;
    private Signature verifier;

    @BeforeAll
    public static void createKeyPair() throws Exception {
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
    public void signThenThenVerify() {
        String json = "{\"min\":18}";
        SignatureData signature = SignatureData.sign(signer, json);
        signature.verify(verifier, json);
    }

    @Test
    public void failToVerify() {
        SignatureData signature = SignatureData.fromString("AAAAAA");
        assertThatThrownBy(() -> signature.verify(verifier, "{\"min\":18}"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("signature verification failed");
    }
}
