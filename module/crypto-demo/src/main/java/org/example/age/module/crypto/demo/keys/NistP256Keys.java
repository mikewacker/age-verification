package org.example.age.module.crypto.demo.keys;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.EllipticCurve;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

/** Converts keys stored in configuration to Java keys. Uses the NIST P-256 curve. */
final class NistP256Keys {

    private static final KeyFactory keyFactory = createKeyFactory();
    static final ECParameterSpec nistP256Spec = createNistP256Spec(); // visible for testing

    /** Converts an {@link EccPrivateKey} to a {@link PrivateKey}. */
    public static PrivateKey toPrivateKey(EccPrivateKey configPrivateKey) {
        KeySpec keySpec = new ECPrivateKeySpec(configPrivateKey.s(), nistP256Spec);
        try {
            return keyFactory.generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    /** Converts an {@link EccPublicKey} to a {@link PublicKey}. */
    public static PublicKey toPublicKey(EccPublicKey configPublicKey) {
        KeySpec keySpec = new ECPublicKeySpec(configPublicKey.w(), nistP256Spec);
        try {
            return keyFactory.generatePublic(keySpec);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    /** Creates the key factory. */
    private static KeyFactory createKeyFactory() {
        try {
            return KeyFactory.getInstance("EC");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /** Creates the specification for the NIST P-256 curve. */
    private static ECParameterSpec createNistP256Spec() {
        BigInteger p = parseHex(" FFFFFFFF 00000001 00000000 00000000 00000000 FFFFFFFF FFFFFFFF FFFFFFFF");
        BigInteger a = parseHex(" FFFFFFFF 00000001 00000000 00000000 00000000 FFFFFFFF FFFFFFFF FFFFFFFC");
        BigInteger b = parseHex(" 5AC635D8 AA3A93E7 B3EBBD55 769886BC 651D06B0 CC53B0F6 3BCE3C3E 27D2604B");
        BigInteger gX = parseHex("6B17D1F2 E12C4247 F8BCE6E5 63A440F2 77037D81 2DEB33A0 F4A13945 D898C296");
        BigInteger gY = parseHex("4FE342E2 FE1A7F9B 8EE7EB4A 7C0F9E16 2BCE3357 6B315ECE CBB64068 37BF51F5");
        BigInteger n = parseHex(" FFFFFFFF 00000000 FFFFFFFF FFFFFFFF BCE6FAAD A7179E84 F3B9CAC2 FC632551");
        int h = 1;
        return new ECParameterSpec(new EllipticCurve(new ECFieldFp(p), a, b), new ECPoint(gX, gY), n, h);
    }

    /** Parses a big integer from a hex value with spaces. */
    private static BigInteger parseHex(String value) {
        String cleanedValue = value.replace(" ", "");
        return new BigInteger(cleanedValue, 16);
    }

    // static class
    private NistP256Keys() {}
}
