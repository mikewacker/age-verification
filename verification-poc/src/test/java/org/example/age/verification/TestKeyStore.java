package org.example.age.verification;

import java.security.KeyPair;
import org.example.age.data.crypto.SecureId;
import org.example.age.testing.crypto.TestKeys;

/** In-memory key store for testing. */
public final class TestKeyStore {

    private static KeyPair avsSigningKeyPair = TestKeys.generateEd25519KeyPair();
    private static SecureId remotePseudonymKey = SecureId.generate();
    private static SecureId localPseudonymKey = SecureId.generate();

    public static KeyPair avsSigningKeyPair() {
        return avsSigningKeyPair;
    }

    public static SecureId remotePseudonymKey() {
        return remotePseudonymKey;
    }

    public static SecureId localPseudonymKey() {
        return localPseudonymKey;
    }

    // static class
    private TestKeyStore() {}
}
