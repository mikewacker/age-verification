package org.example.age.data.crypto;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

/**
 * Utilities for digital signatures.
 *
 * <p>Only {@code Ed25519} keys are supported.</p>
 */
final class SignatureUtils {

    /** Signs the message, returning the signature. */
    public static byte[] sign(byte[] message, PrivateKey privateKey) {
        try {
            Signature signer = Signatures.createSigner(privateKey);
            signer.update(message);
            return signer.sign();
        } catch (Exception e) {
            throw new RuntimeException("signing failed", e);
        }
    }

    /** Verifies the signature against the message, returning whether verification succeeded. */
    public static boolean verify(byte[] message, byte[] signature, PublicKey publicKey) {
        try {
            Signature verifier = Signatures.createVerifier(publicKey);
            verifier.update(message);
            return verifier.verify(signature);
        } catch (Exception e) {
            return false;
        }
    }

    // static class
    private SignatureUtils() {}

    /** Creates {@link Signature}'s. */
    private static final class Signatures {

        private static final String ALGORITHM = "Ed25519";

        /** Creates a signer. */
        public static Signature createSigner(PrivateKey privateKey) throws InvalidKeyException {
            Signature signer = newSignatureObject();
            signer.initSign(privateKey);
            return signer;
        }

        /** Creates a verifier. */
        public static Signature createVerifier(PublicKey publicKey) throws InvalidKeyException {
            Signature verifier = newSignatureObject();
            verifier.initVerify(publicKey);
            return verifier;
        }

        /** Creates an uninitialized signer or verifier. */
        private static Signature newSignatureObject() {
            try {
                return Signature.getInstance(ALGORITHM);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("unexpected error", e);
            }
        }

        // static class
        private Signatures() {}
    }
}
