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
 * Utilities for digital signatures.
 *
 * <p>Only {@code Ed25519} keys are supported.</p>
 */
final class SignatureUtils {

    public static final int MESSAGE_OFFSET = 4;

    private static final int UNSIGNED_MASK = 0x7FFFFFFF;

    /** Signs the message, returning the signature. */
    public static byte[] sign(byte[] message, PrivateKey privateKey) {
        Signature signer = Signatures.createSigner(privateKey);
        try {
            signer.update(message);
            return signer.sign();
        } catch (SignatureException e) {
            throw new RuntimeException(e);
        }
    }

    /** Verifies the signature against the message, returning whether verification succeeded. */
    public static boolean verify(byte[] message, byte[] signature, PublicKey publicKey) {
        Signature verifier = Signatures.createVerifier(publicKey);
        try {
            verifier.update(message);
            return verifier.verify(signature);
        } catch (SignatureException e) {
            return false;
        }
    }

    /** Signs the message, returning the message with the signature appended. */
    public static byte[] signLegacy(byte[] message, PrivateKey privateKey) {
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
    public static int verifyLegacy(byte[] signedMessage, PublicKey publicKey) {
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
            Signature signer = createSignatureObject();
            try {
                signer.initSign(privateKey);
                return signer;
            } catch (InvalidKeyException e) {
                throw new IllegalArgumentException("key must be an ed25519 key", e);
            }
        }

        /** Creates a verifier. */
        public static Signature createVerifier(PublicKey publicKey) {
            Signature verifier = createSignatureObject();
            try {
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
