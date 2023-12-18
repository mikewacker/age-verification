package org.example.age.service.key.common;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.NoSuchElementException;
import org.example.age.data.crypto.SecureId;

/**
 * Provides signing keys and pseudonym keys.
 *
 * <p>The keys provided may be refreshed.</p>
 *
 * <p>Signing keys must be {@code Ed25519} keys. A site will not have the private signing key.</p>
 *
 * <p>It is not expected that a key would be retrieved that does not exist;
 * if this occurs, a {@link NoSuchElementException} will be thrown.</p>
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
