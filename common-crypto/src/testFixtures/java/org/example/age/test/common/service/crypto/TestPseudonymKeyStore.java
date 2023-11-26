package org.example.age.test.common.service.crypto;

import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.common.service.crypto.PseudonymKeyProvider;
import org.example.age.data.crypto.SecureId;

/** Key store that generates pseudonym keys on demand. */
@Singleton
final class TestPseudonymKeyStore implements PseudonymKeyProvider {

    private final Map<String, SecureId> keyStore = new HashMap<>();

    @Inject
    public TestPseudonymKeyStore() {}

    @Override
    public SecureId get(String name) {
        return keyStore.computeIfAbsent(name, n -> SecureId.generate());
    }
}
