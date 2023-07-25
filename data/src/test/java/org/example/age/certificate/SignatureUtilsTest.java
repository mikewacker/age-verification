package org.example.age.certificate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.util.Arrays;
import org.assertj.core.api.ThrowableAssert;
import org.example.age.testing.TestKeys;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class SignatureUtilsTest {

    private static final byte[] MESSAGE = "Hello, world!".getBytes(StandardCharsets.UTF_8);

    private static KeyPair keyPair;
    private static KeyPair otherKeyPair;
    private static KeyPair invalidKeyPair;

    @BeforeAll
    public static void generateKeys() {
        keyPair = TestKeys.generateEd25519KeyPair();
        otherKeyPair = TestKeys.generateEd25519KeyPair();
        invalidKeyPair = generateNistP256KeyPair();
    }

    @Test
    public void signThenVerify() {
        byte[] signedMessage = SignatureUtils.sign(MESSAGE, keyPair.getPrivate());
        int messageLength = SignatureUtils.verify(signedMessage, keyPair.getPublic());
        ByteBuffer messageBuffer = ByteBuffer.wrap(MESSAGE);
        ByteBuffer verifiedMessageBuffer = ByteBuffer.wrap(signedMessage, SignatureUtils.MESSAGE_OFFSET, messageLength);
        assertThat(verifiedMessageBuffer).isEqualTo(messageBuffer);
    }

    @Test
    public void error_Verify_TamperedMessage() {
        byte[] signedMessage = SignatureUtils.sign(MESSAGE, keyPair.getPrivate());
        tamperMessage(signedMessage);
        error_Verify(signedMessage, "invalid signature");
    }

    @Test
    public void error_Verify_TamperedSignature() {
        byte[] signedMessage = SignatureUtils.sign(MESSAGE, keyPair.getPrivate());
        tamperSignature(signedMessage);
        error_Verify(signedMessage, "invalid signature");
    }

    @Test
    public void error_Verify_WrongSigner() {
        byte[] signedMessage = SignatureUtils.sign(MESSAGE, otherKeyPair.getPrivate());
        error_Verify(signedMessage, "invalid signature");
    }

    @Test
    public void error_Verify_TooShort() {
        byte[] signedMessage = new byte[1];
        error_Verify(signedMessage, "signed message is too short");

        signedMessage = createTooShortSignedMessage();
        error_Verify(signedMessage, "signed message is too short");
    }

    private void error_Verify(byte[] signedMessage, String expectedMessage) {
        assertThatThrownBy(() -> SignatureUtils.verify(signedMessage, keyPair.getPublic()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);
    }

    @Test
    public void error_InvalidKey_Sign() {
        error_InvalidKey(() -> SignatureUtils.sign(MESSAGE, invalidKeyPair.getPrivate()));
    }

    @Test
    public void error_InvalidKey_Verify() {
        byte[] signedMessage = new byte[1024];
        error_InvalidKey(() -> SignatureUtils.verify(signedMessage, invalidKeyPair.getPublic()));
    }

    private void error_InvalidKey(ThrowableAssert.ThrowingCallable callable) {
        assertThatThrownBy(callable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("key must be an ed25519 key");
    }

    private static void tamperMessage(byte[] signedMessage) {
        signedMessage[SignatureUtils.MESSAGE_OFFSET] = 0;
    }

    private static void tamperSignature(byte[] signedMessage) {
        Arrays.fill(signedMessage, signedMessage.length - 4, signedMessage.length, (byte) 0);
    }

    private static byte[] createTooShortSignedMessage() {
        ByteBuffer signedMessageBuffer = ByteBuffer.allocate(SignatureUtils.MESSAGE_OFFSET + 4);
        signedMessageBuffer.putInt(4); // signature length
        return signedMessageBuffer.array();
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
