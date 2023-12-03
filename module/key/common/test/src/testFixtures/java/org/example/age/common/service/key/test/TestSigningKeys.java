package org.example.age.common.service.key.test;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.example.age.data.crypto.SigningKeys;

/** Test {@link PrivateKey}'s and {@link PublicKey}'s for digital signatures. */
final class TestSigningKeys {

    private static final KeyPair keyPair = SigningKeys.generateEd25519KeyPair();

    /** Gets the private signing key. */
    public static PrivateKey privateKey() {
        return keyPair.getPrivate();
    }

    /** Gets the public signing key. */
    public static PublicKey publicKey() {
        return keyPair.getPublic();
    }

    // static class
    private TestSigningKeys() {}
}
