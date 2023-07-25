package org.example.age.testing;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

/** Signs message and verifies signatures to check that a signing key works. */
public final class TestSigning {

    private static final String ALGORITHM = "Ed25519";

    /** Signs a message using the Ed25519 signature algorithm. */
    public static byte[] signEd25519(byte[] message, PrivateKey privateKey) throws Exception {
        Signature signer = Signature.getInstance(ALGORITHM);
        signer.initSign(privateKey);
        signer.update(message);
        return signer.sign();
    }

    /** Verifies a signature using the Ed25519 signature algorithm. */
    public static boolean verifyEd25519(byte[] message, byte[] signature, PublicKey publicKey) throws Exception {
        Signature verifier = Signature.getInstance(ALGORITHM);
        verifier.initVerify(publicKey);
        verifier.update(message);
        return verifier.verify(signature);
    }

    // static class
    private TestSigning() {}
}
