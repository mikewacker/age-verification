package org.example.age.common.api.data;

import org.example.age.data.crypto.Aes256Key;
import org.example.age.data.crypto.AesGcmEncryptionPackage;

/**
 * Authentication data that is used to determine if two actions were performed by the same person.
 *
 * <p>This authentication check is done on a best-effort basis,
 * subject to tradeoffs involving privacy, false positives, etc.</p>
 */
public interface AuthMatchData {

    /** Matches two sets of data, determining if they came from the same person. */
    boolean match(AuthMatchData other);

    /** Encrypts the data into an {@link AesGcmEncryptionPackage} (or returns an empty token). */
    AesGcmEncryptionPackage encrypt(Aes256Key key);
}
