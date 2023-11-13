package org.example.age.testing.crypto;

import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

/** Encrypts and decrypts messages to check that an encryption key works. */
public final class TestEncrypting {

    private static final SecureRandom random = new SecureRandom();

    /** Encrypts a plaintext using AES/GCM, returning the ciphertext. */
    public static byte[] encryptAesGcm(byte[] plaintext, SecretKey key, byte[] iv) throws Exception {
        Cipher cipher = createAesGcmCipher(Cipher.ENCRYPT_MODE, key, iv);
        return cipher.doFinal(plaintext);
    }

    /** Decrypts a ciphertext using AES/GCM, returning the plaintext. */
    public static byte[] decryptAesGcm(byte[] ciphertext, SecretKey key, byte[] iv) throws Exception {
        Cipher cipher = createAesGcmCipher(Cipher.DECRYPT_MODE, key, iv);
        return cipher.doFinal(ciphertext);
    }

    /** Creates an IV for GCM mode. */
    public static byte[] createGcmIv() {
        byte[] iv = new byte[12];
        random.nextBytes(iv);
        return iv;
    }

    /** Creates an AES/GCM cipher. */
    private static Cipher createAesGcmCipher(int opmode, SecretKey key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        AlgorithmParameterSpec gcmParamSpec = new GCMParameterSpec(128, iv);
        cipher.init(opmode, key, gcmParamSpec);
        return cipher;
    }

    // static class
    private TestEncrypting() {}
}
