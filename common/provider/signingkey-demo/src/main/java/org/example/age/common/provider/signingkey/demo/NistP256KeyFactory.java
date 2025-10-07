package org.example.age.common.provider.signingkey.demo;

import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

/** Factory for NIST P-256 keys. */
public final class NistP256KeyFactory {

    private static final KeyFactory keyFactory = createKeyFactory();
    private static final ECParameterSpec nistP256Spec = createNistP256Spec();

    /** Creates a private key. */
    public static PrivateKey createPrivate(BigInteger s) {
        KeySpec privateKeySpec = new ECPrivateKeySpec(s, nistP256Spec);
        try {
            return keyFactory.generatePrivate(privateKeySpec);
        } catch (InvalidKeySpecException e) {
            throw new IllegalArgumentException("invalid key", e);
        }
    }

    /** Creates a public key. */
    public static PublicKey createPublic(ECPoint w) {
        KeySpec publicKeySpec = new ECPublicKeySpec(w, nistP256Spec);
        try {
            return keyFactory.generatePublic(publicKeySpec);
        } catch (InvalidKeySpecException e) {
            throw new IllegalArgumentException("invalid key", e);
        }
    }

    /** Creates the key factory. */
    private static KeyFactory createKeyFactory() {
        try {
            return KeyFactory.getInstance("EC");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** Creates the NIST P-256 specification. */
    private static ECParameterSpec createNistP256Spec() {
        try {
            AlgorithmParameters params = AlgorithmParameters.getInstance("EC");
            params.init(new ECGenParameterSpec("secp256r1"));
            return params.getParameterSpec(ECParameterSpec.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private NistP256KeyFactory() {} // static class
}
