package org.example.age.data.crypto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Optional;
import org.example.age.data.crypto.internal.EncryptionUtils;
import org.example.age.data.utils.DataStyle;
import org.immutables.value.Value;

/** Encryption package for plaintext encrypted using AES-256/GCM. */
@Value.Immutable
@DataStyle
@JsonSerialize(as = ImmutableAesGcmEncryptionPackage.class)
@JsonDeserialize(as = ImmutableAesGcmEncryptionPackage.class)
public interface AesGcmEncryptionPackage {

    /** Creates an encryption package from the ciphertext and the IV. */
    static AesGcmEncryptionPackage of(BytesValue ciphertext, BytesValue iv) {
        return ImmutableAesGcmEncryptionPackage.builder()
                .ciphertext(ciphertext)
                .iv(iv)
                .build();
    }

    /** Encrypts the plaintext using the key (and a generated IV). */
    static AesGcmEncryptionPackage encrypt(byte[] rawPlaintext, Aes256Key key) {
        byte[] rawIv = EncryptionUtils.createIv();
        byte[] rawCiphertext = EncryptionUtils.encrypt(rawPlaintext, key.uncopiedBytes(), rawIv);
        return of(BytesValue.ofBytes(rawCiphertext), BytesValue.ofBytes(rawIv));
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
