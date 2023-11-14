package org.example.age.data.crypto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.util.Arrays;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class SignatureUtilsTest {

    private static final byte[] MESSAGE = "Hello, world!".getBytes(StandardCharsets.UTF_8);

    private static KeyPair keyPair;
    private static KeyPair otherKeyPair;
    private static KeyPair invalidKeyPair;

    @BeforeAll
    public static void generateKeys() {
        keyPair = SigningKeys.generateEd25519KeyPair();
        otherKeyPair = SigningKeys.generateEd25519KeyPair();
        invalidKeyPair = generateNistP256KeyPair();
    }

    @Test
    public void signThenVerify() {
        byte[] signature = SignatureUtils.sign(MESSAGE, keyPair.getPrivate());
        boolean wasVerified = SignatureUtils.verify(MESSAGE, signature, keyPair.getPublic());
        assertThat(wasVerified).isTrue();
    }

    @Test
    public void verifyFailed_TamperedMessage() {
        byte[] signature = SignatureUtils.sign(MESSAGE, keyPair.getPrivate());
        byte[] tamperedMessage = tamper(MESSAGE);
        boolean wasVerified = SignatureUtils.verify(tamperedMessage, signature, keyPair.getPublic());
        assertThat(wasVerified).isFalse();
    }

    @Test
    public void verifyFailed_TamperedSignature() {
        byte[] signature = SignatureUtils.sign(MESSAGE, keyPair.getPrivate());
        byte[] tamperedSignature = tamper(signature);
        boolean wasVerified = SignatureUtils.verify(MESSAGE, tamperedSignature, keyPair.getPublic());
        assertThat(wasVerified).isFalse();
    }

    @Test
    public void verifyFailed_WrongKey() {
        byte[] signature = SignatureUtils.sign(MESSAGE, keyPair.getPrivate());
        boolean wasVerified = SignatureUtils.verify(MESSAGE, signature, otherKeyPair.getPublic());
        assertThat(wasVerified).isFalse();
    }

    @Test
    public void error_InvalidKey_Sign() {
        error_InvalidKey(() -> SignatureUtils.sign(MESSAGE, invalidKeyPair.getPrivate()));
    }

    @Test
    public void error_InvalidKey_Verify() {
        byte[] signature = new byte[1024];
        error_InvalidKey(() -> SignatureUtils.verify(MESSAGE, signature, invalidKeyPair.getPublic()));
    }

    private void error_InvalidKey(ThrowableAssert.ThrowingCallable callable) {
        assertThatThrownBy(callable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("key must be an ed25519 key");
    }

    private static byte[] tamper(byte[] bytes) {
        byte[] tamperedBytes = Arrays.copyOf(bytes, bytes.length);
        Arrays.fill(tamperedBytes, 0, 4, (byte) 0);
        return tamperedBytes;
    }

    private static KeyPair generateNistP256KeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
            AlgorithmParameterSpec nistP256Params = new ECGenParameterSpec("secp256r1");
            keyPairGenerator.initialize(nistP256Params);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }
}
