package org.example.age.testing;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

/** Generates keys for testing. */
public final class TestKeys {

    /** Generates an Ed25519 key pair. */
    public static KeyPair generateEd25519KeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("Ed25519");
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // static class
    private TestKeys() {}
}
