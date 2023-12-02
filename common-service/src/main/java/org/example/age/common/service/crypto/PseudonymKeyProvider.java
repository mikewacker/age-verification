package org.example.age.common.service.crypto;

import org.example.age.data.crypto.SecureId;

/** Provides {@link SecureId} keys used to localize {@link SecureId} pseudonyms. */
@FunctionalInterface
public interface PseudonymKeyProvider {

    SecureId get(String name);
}
