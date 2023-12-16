package org.example.age.data.crypto;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

/** Generates keys used for signatures. */
public final class SigningKeys {

    private static KeyPairGenerator ed25519KeyPairGenerator = createEd25519KeyPairGenerator();

    /** Generates an {@code Ed25519} key pair. */
    public static KeyPair generateEd25519KeyPair() {
        return ed25519KeyPairGenerator.generateKeyPair();
    }

    /** Creates a key pair generator for {@code Ed25519} keys. */
    private static KeyPairGenerator createEd25519KeyPairGenerator() {
        try {
            return KeyPairGenerator.getInstance("Ed25519");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("unexpected error", e);
        }
    }

    // static class
    private SigningKeys() {}
}
