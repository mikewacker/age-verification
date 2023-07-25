package org.example.age.testing;

import java.security.KeyPair;
import org.example.age.data.SecureId;

/** In-memory key store for testing. */
public final class TestKeyStore {

    private static KeyPair avsSigningKeyPair = TestKeys.generateEd25519KeyPair();
    private static SecureId remotedSiteIdKey = SecureId.generate();
    private static SecureId localSiteIdKey = SecureId.generate();

    public static KeyPair avsSigningKeyPair() {
        return avsSigningKeyPair;
    }

    public static SecureId remoteSiteIdKey() {
        return remotedSiteIdKey;
    }

    public static SecureId localSiteIdKey() {
        return localSiteIdKey;
    }

    // static class
    private TestKeyStore() {}
}
