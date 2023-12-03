package org.example.age.common.service.key.test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.common.service.key.PseudonymKeyProvider;
import org.example.age.data.crypto.SecureId;

@Singleton
final class TestPseudonymKeys implements PseudonymKeyProvider {

    private final Map<String, SecureId> keys = new ConcurrentHashMap<>();

    @Inject
    public TestPseudonymKeys() {}

    @Override
    public SecureId get(String name) {
        return keys.computeIfAbsent(name, n -> SecureId.generate());
    }
}
