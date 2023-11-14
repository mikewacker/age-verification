package org.example.age.data.certificate;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import java.nio.ByteBuffer;
import java.util.Arrays;

/** Legacy utilities for packaging bytes together. */
public final class PackageUtils {

    private static final int MESSAGE_OFFSET = 4;
    private static final int CIPHERTEXT_OFFSET = 12;

    private static final int UNSIGNED_MASK = 0x7FFFFFFF;

    /** Creates a signed message from the message and the signature. */
    public static byte[] createSignedMessage(byte[] message, byte[] signature) {
        return Bytes.concat(Ints.toByteArray(signature.length), message, signature);
    }

    /** Extracts the message and the signature from the signed message. */
    public static SignedMessage parseSignedMessage(byte[] signedMessage) {
        int signatureOffset = getSignatureOffset(signedMessage);
        byte[] message = Arrays.copyOfRange(signedMessage, MESSAGE_OFFSET, signatureOffset);
        byte[] signature = Arrays.copyOfRange(signedMessage, signatureOffset, signedMessage.length);
        return new SignedMessage(message, signature);
    }

    /** Creates an encryption package from the ciphertext and the IV. */
    public static byte[] createEncryptionPackage(byte[] ciphertext, byte[] iv) {
        return Bytes.concat(iv, ciphertext);
    }

    /** Extracts the ciphertext and the IV from the encryption package. */
    public static EncryptionPackage parseEncryptionPackage(byte[] encryptionPackage) {
        if (encryptionPackage.length < CIPHERTEXT_OFFSET) {
            throw new IllegalArgumentException("encryption package is too short");
        }

        byte[] ciphertext = Arrays.copyOfRange(encryptionPackage, CIPHERTEXT_OFFSET, encryptionPackage.length);
        byte[] iv = Arrays.copyOfRange(encryptionPackage, 0, CIPHERTEXT_OFFSET);
        return new EncryptionPackage(ciphertext, iv);
    }

    /** Gets the offset of the signature in a signed message. */
    private static int getSignatureOffset(byte[] signedMessage) {
        if (signedMessage.length <= MESSAGE_OFFSET) {
            throw new IllegalArgumentException("signed message is too short");
        }

        ByteBuffer buffer = ByteBuffer.wrap(signedMessage);
        int signatureLength = buffer.getInt() & UNSIGNED_MASK;
        int messageLength = signedMessage.length - MESSAGE_OFFSET - signatureLength;
        if (messageLength <= 0) {
            throw new IllegalArgumentException("signed message is too short");
        }

        return MESSAGE_OFFSET + messageLength;
    }

    public record SignedMessage(byte[] message, byte[] signature) {}

    public record EncryptionPackage(byte[] ciphertext, byte[] iv) {}
}
