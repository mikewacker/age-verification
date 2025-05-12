package org.example.age.module.crypto.demo.testing;

import java.math.BigInteger;
import org.example.age.module.crypto.demo.keys.EccPrivateKey;
import org.example.age.module.crypto.demo.keys.EccPublicKey;

/** Key pair from configuration for testing. */
public final class ConfigKeyPair {

    private static final EccPrivateKey privateKey = EccPrivateKey.of(
            new BigInteger("87808632867103956881705523559918117434194472117688001288631494927155518459976"));
    private static final EccPublicKey publicKey = EccPublicKey.of(
            new BigInteger("61340499596180719707288738669477306360190613239883629564918816825111167687915"),
            new BigInteger("38000387743223524528339467703153930999010297887656121516318277573781881204945"));

    /** Gets the private key. */
    public static EccPrivateKey privateKey() {
        return privateKey;
    }

    /** Gets the public key. */
    public static EccPublicKey publicKey() {
        return publicKey;
    }

    // static class
    private ConfigKeyPair() {}
}
