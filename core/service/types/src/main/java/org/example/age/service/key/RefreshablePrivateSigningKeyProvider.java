package org.example.age.service.key;

import java.security.PrivateKey;

/**
 * Provides a private signing key.
 *
 * <p>The key provided may be refreshed.</p>
 *
 * <p>The signing key must be an {@code Ed25519} key.</p>
 */
@FunctionalInterface
public interface RefreshablePrivateSigningKeyProvider {

    PrivateKey getPrivateSigningKey();
}
