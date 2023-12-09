package org.example.age.data.crypto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.example.age.data.json.JsonStyle;
import org.immutables.value.Value;

/** Encryption package for plaintext encrypted using AES-256/GCM. */
@Value.Immutable
@JsonStyle
@JsonDeserialize(as = ImmutableAesGcmEncryptionPackage.class)
public interface AesGcmEncryptionPackage {

    /** Creates an encryption package from the ciphertext and the IV. */
    static AesGcmEncryptionPackage of(BytesValue ciphertext, BytesValue iv) {
        return ImmutableAesGcmEncryptionPackage.builder()
                .ciphertext(ciphertext)
                .iv(iv)
                .build();
    }

    /** Creates an empty encryption package, which would fail to decrypt. */
    static AesGcmEncryptionPackage empty() {
        return of(BytesValue.empty(), BytesValue.empty());
    }

    /** Encrypts the plaintext using the key (and a generated IV). */
    static AesGcmEncryptionPackage encrypt(byte[] rawPlaintext, Aes256Key key) {
        byte[] iv = EncryptionUtils.createIv();
        byte[] ciphertext = EncryptionUtils.encrypt(rawPlaintext, key.uncopiedBytes(), iv);
        return of(BytesValue.ofUncopiedBytes(ciphertext), BytesValue.ofUncopiedBytes(iv));
    }

    /** Encrypted ciphertext. */
    BytesValue ciphertext();

    /** Initialization vector. */
    BytesValue iv();

    /** Decrypts the ciphertext using the key (and the IV), or returns empty if decryption fails. */
    default Optional<byte[]> tryDecrypt(Aes256Key key) {
        return EncryptionUtils.tryDecrypt(ciphertext().uncopiedBytes(), key.uncopiedBytes(), iv().uncopiedBytes());
    }
}
