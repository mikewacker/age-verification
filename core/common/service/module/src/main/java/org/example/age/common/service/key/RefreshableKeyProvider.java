package org.example.age.common.service.key;

import java.security.PrivateKey;
import java.security.PublicKey;
import org.example.age.data.crypto.SecureId;

/**
 * Provides signing keys and pseudonym keys.
 *
 * <p>The keys provided may be refreshed.</p>
 *
 * <p>A site will not have a private signing key, but it will have the other types of keys.</p>
 *
 * <p>Signing keys must be {@code Ed25519} keys.</p>
 */
public interface RefreshableKeyProvider {

    /**
     * Gets the private signing key.
     *
     * <p>Only the age verification service will have this key.</p>
     */
    PrivateKey getPrivateSigningKey();

    /** Gets the public signing key. */
    PublicKey getPublicSigningKey();

    /** Gets a pseudonym key. */
    SecureId getPseudonymKey(String name);
}
