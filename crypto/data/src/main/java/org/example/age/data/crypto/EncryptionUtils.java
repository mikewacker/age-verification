package org.example.age.data.crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Optional;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/** Utilities for encryption using AES/GCM. */
final class EncryptionUtils {

    /** Encrypts the plaintext using the key and the IV, returning the ciphertext. */
    public static byte[] encrypt(byte[] plaintext, byte[] key, byte[] iv) {
        try {
            Cipher encryptor = Ciphers.createEncryptor(key, iv);
            return encryptor.doFinal(plaintext);
        } catch (Exception e) {
            throw new RuntimeException("encryption failed", e);
        }
    }

    /** Decrypts the ciphertext using the key and the IV, returning the plaintext, or empty if decryption fails. */
    public static Optional<byte[]> tryDecrypt(byte[] ciphertext, byte[] key, byte[] iv) {
        try {
            Cipher decryptor = Ciphers.createDecryptor(key, iv);
            byte[] plaintext = decryptor.doFinal(ciphertext);
            return Optional.of(plaintext);
        } catch (Exception e) {
            // most likely, an AEADBadTagException when the authentication tag cannot be verified
            return Optional.empty();
        }
    }

    /** Creates an IV. */
    public static byte[] createIv() {
        return SecureRandomUtils.generateBytes(Ciphers.IV_LENGTH_BYTES);
    }

    // static class
    private EncryptionUtils() {}

    /** Creates {@link Cipher}'s. */
    private static final class Ciphers {

        private static final String ALGORITHM = "AES/GCM/NoPadding";
        private static final String KEY_ALGORITHM = "AES";
        private static final int IV_LENGTH_BYTES = 12;
        private static final int TAG_LENGTH_BITS = 128;

        /** Creates an encryptor. */
        public static Cipher createEncryptor(byte[] rawKey, byte[] rawIv)
                throws InvalidKeyException, InvalidAlgorithmParameterException {
            Cipher encryptor = newCipher();
            SecretKey key = createKey(rawKey);
            AlgorithmParameterSpec gcmParam = createGcmParam(rawIv);
            encryptor.init(Cipher.ENCRYPT_MODE, key, gcmParam);
            return encryptor;
        }

        /** Creates a decryptor. */
        public static Cipher createDecryptor(byte[] rawKey, byte[] rawIv)
                throws InvalidKeyException, InvalidAlgorithmParameterException {
            Cipher decryptor = newCipher();
            SecretKey key = createKey(rawKey);
            AlgorithmParameterSpec gcmParam = createGcmParam(rawIv);
            decryptor.init(Cipher.DECRYPT_MODE, key, gcmParam);
            return decryptor;
        }

        /** Creates an uninitialized encryptor or decryptor. */
        private static Cipher newCipher() {
            try {
                return Cipher.getInstance(ALGORITHM);
            } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
                throw new RuntimeException("unexpected error", e);
            }
        }

        /** Creates a key from the raw bytes. */
        private static SecretKey createKey(byte[] rawKey) {
            return new SecretKeySpec(rawKey, KEY_ALGORITHM);
        }

        /** Creates a GCM parameter from the raw bytes for the IV. */
        private static AlgorithmParameterSpec createGcmParam(byte[] rawIv) {
            return new GCMParameterSpec(TAG_LENGTH_BITS, rawIv);
        }
    }
}
