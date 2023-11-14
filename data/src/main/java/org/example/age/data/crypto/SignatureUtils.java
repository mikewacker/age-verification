package org.example.age.data.crypto;

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
public final class SignatureUtils {

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

    // static class
    private SignatureUtils() {}

    /** Creates {@link Signature}'s. */
    private static final class Signatures {

        private static final String ALGORITHM = "Ed25519";

        /** Creates a signer. */
        public static Signature createSigner(PrivateKey privateKey) {
            Signature signer = newSignatureObject();
            try {
                signer.initSign(privateKey);
                return signer;
            } catch (InvalidKeyException e) {
                throw new IllegalArgumentException("key must be an ed25519 key", e);
            }
        }

        /** Creates a verifier. */
        public static Signature createVerifier(PublicKey publicKey) {
            Signature verifier = newSignatureObject();
            try {
                verifier.initVerify(publicKey);
                return verifier;
            } catch (InvalidKeyException e) {
                throw new IllegalArgumentException("key must be an ed25519 key", e);
            }
        }

        /** Creates an uninitialized signer or verifier. */
        private static Signature newSignatureObject() {
            try {
                return Signature.getInstance(ALGORITHM);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }

        // static class
        private Signatures() {}
    }
}
