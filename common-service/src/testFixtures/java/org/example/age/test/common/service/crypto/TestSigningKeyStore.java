package org.example.age.test.common.service.crypto;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.example.age.data.crypto.SigningKeys;

/** Key store that contains a single Ed25519 key pair. */
final class TestSigningKeyStore {

    private static final KeyPair keyPair = SigningKeys.generateEd25519KeyPair();

    /** Gets the private signing key. */
    public static PrivateKey getPrivateKey() {
        return keyPair.getPrivate();
    }

    /** Gets the public signing key. */
    public static PublicKey getPublicKey() {
        return keyPair.getPublic();
    }

    // static class
    private TestSigningKeyStore() {}
}
