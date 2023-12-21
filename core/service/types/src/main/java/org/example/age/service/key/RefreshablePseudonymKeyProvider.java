package org.example.age.service.key;

import java.util.NoSuchElementException;
import org.example.age.data.crypto.SecureId;

/**
 * Provides pseudonym keys.
 *
 * <p>The keys provided may be refreshed.</p>
 *
 * <p>It is not expected that a key would be retrieved that does not exist;
 * if this occurs, a {@link NoSuchElementException} will be thrown.</p>
 */
@FunctionalInterface
public interface RefreshablePseudonymKeyProvider {

    SecureId getPseudonymKey(String name);
}
