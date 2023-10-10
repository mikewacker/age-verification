package org.example.age.data.certificate;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

/**
 * Utilities for digital signatures, where the signature is appended to the message.
 *
 * <p>Only Ed25519 keys are supported, but no assumptions are made about the signature length,
 * should other types of keys be supported later.</p>
 */
final class SignatureUtils {

    public static final int MESSAGE_OFFSET = 4;

    private static final int UNSIGNED_MASK = 0x7FFFFFFF;

    /** Signs the message, returning the message with the signature appended. */
    public static byte[] sign(byte[] message, PrivateKey privateKey) {
        Signature signer = Signatures.createSigner(privateKey);
        try {
            signer.update(message);
            byte[] signature = signer.sign();
            return Bytes.concat(Ints.toByteArray(signature.length), message, signature);
        } catch (SignatureException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Verifies the signature of the message, returning the length of the message.
     * Throws an {@link IllegalArgumentException} if verification fails.
     */
    public static int verify(byte[] signedMessage, PublicKey publicKey) {
        // Calculate offsets and lengths.
        if (signedMessage.length <= MESSAGE_OFFSET) {
            throw new IllegalArgumentException("signed message is too short");
        }

        ByteBuffer buffer = ByteBuffer.wrap(signedMessage);
        int signatureLength = buffer.getInt() & UNSIGNED_MASK;
        int messageLength = signedMessage.length - MESSAGE_OFFSET - signatureLength;
        if (messageLength <= 0) {
            throw new IllegalArgumentException("signed message is too short");
        }

        int signatureOffset = MESSAGE_OFFSET + messageLength;

        // Verify.
        Signature verifier = Signatures.createVerifier(publicKey);
        try {
            verifier.update(signedMessage, MESSAGE_OFFSET, messageLength);
            boolean wasVerified = verifier.verify(signedMessage, signatureOffset, signatureLength);
            if (!wasVerified) {
                throw new IllegalArgumentException("invalid signature");
            }
        } catch (SignatureException e) {
            throw new RuntimeException(e);
        }
        return messageLength;
    }

    // static class
    private SignatureUtils() {}

    /** Creates {@link Signature}'s. */
    private static final class Signatures {

        /** Creates a signer. */
        public static Signature createSigner(PrivateKey privateKey) {
            try {
                Signature signer = createSignatureObject();
                signer.initSign(privateKey);
                return signer;
            } catch (InvalidKeyException e) {
                throw new IllegalArgumentException("key must be an ed25519 key", e);
            }
        }

        /** Creates a verifier. */
        public static Signature createVerifier(PublicKey publicKey) {
            try {
                Signature verifier = createSignatureObject();
                verifier.initVerify(publicKey);
                return verifier;
            } catch (InvalidKeyException e) {
                throw new IllegalArgumentException("key must be an ed25519 key", e);
            }
        }

        /** Creates a signature object for signing or verifying. */
        private static Signature createSignatureObject() {
            try {
                return Signature.getInstance("Ed25519");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }

        // static class
        private Signatures() {}
    }
}
