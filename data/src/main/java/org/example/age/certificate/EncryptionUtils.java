package org.example.age.certificate;

import com.google.common.primitives.Bytes;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

/** Utilities for encryption using AES/GCM. */
final class EncryptionUtils {

    private static final int IV_LENGTH = 12;

    private static final SecureRandom random = new SecureRandom();

    /** Encrypts the plaintext using AES/GCM, returning the encryption package. */
    public static byte[] encrypt(byte[] plaintext, SecretKey key) {
        byte[] iv = createIv();
        Cipher encrypter = Ciphers.createEncryptor(key, iv);
        try {
            byte[] ciphertext = encrypter.doFinal(plaintext);
            return Bytes.concat(iv, ciphertext);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new IllegalArgumentException("encryption failed", e);
        }
    }

    /** Decrypts the encryption package using AES/GCM, returning the plaintext. */
    public static byte[] decrypt(byte[] encryptionPackage, SecretKey key) {
        // Calculate offsets and lengths.
        int ciphertextOffset = IV_LENGTH;
        int ciphertextLength = encryptionPackage.length - ciphertextOffset;
        if (ciphertextLength <= 0) {
            throw new IllegalArgumentException("encryption package is too short");
        }

        // Decrypt.
        Cipher decrypter = Ciphers.createDecryptor(key, encryptionPackage, 0);
        try {
            return decrypter.doFinal(encryptionPackage, ciphertextOffset, ciphertextLength);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new IllegalArgumentException("decryption failed", e);
        }
    }

    /** Creates an IV for GCM mode. */
    private static byte[] createIv() {
        byte[] iv = new byte[IV_LENGTH];
        random.nextBytes(iv);
        return iv;
    }

    // static class
    private EncryptionUtils() {}

    /** Creates {@link Cipher}'s. */
    private static final class Ciphers {

        /** Creates an AES/GCM encryptor. */
        public static Cipher createEncryptor(SecretKey key, byte[] iv) {
            return createCipher(Cipher.ENCRYPT_MODE, key, iv, 0);
        }

        /** Creates an AES/GCM decryptor. */
        public static Cipher createDecryptor(SecretKey key, byte[] buffer, int ivOffset) {
            return createCipher(Cipher.DECRYPT_MODE, key, buffer, ivOffset);
        }

        /** Creates an AES/GCM cipher. */
        private static Cipher createCipher(int opmode, SecretKey key, byte[] buffer, int ivOffset) {
            try {
                Cipher encrypter = Cipher.getInstance("AES/GCM/NoPadding");
                AlgorithmParameterSpec gcmParamSpec = new GCMParameterSpec(128, buffer, ivOffset, IV_LENGTH);
                encrypter.init(opmode, key, gcmParamSpec);
                return encrypter;
            } catch (InvalidKeyException e) {
                throw new IllegalArgumentException("key must be a valid AES key");
            } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException e) {
                throw new RuntimeException("unexpected error", e);
            }
        }
    }
}
