package org.example.age.module.key.common.test;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.data.crypto.SecureId;
import org.example.age.data.crypto.SigningKeys;
import org.example.age.service.module.key.common.RefreshableKeyProvider;

@Singleton
final class TestKeyProvider implements RefreshableKeyProvider {

    private static final KeyPair signingKeyPair = SigningKeys.generateEd25519KeyPair();

    private final Map<String, SecureId> pseudonymKeys = new ConcurrentHashMap<>();

    @Inject
    public TestKeyProvider() {}

    @Override
    public PrivateKey getPrivateSigningKey() {
        return signingKeyPair.getPrivate();
    }

    @Override
    public PublicKey getPublicSigningKey() {
        return signingKeyPair.getPublic();
    }

    @Override
    public SecureId getPseudonymKey(String name) {
        return pseudonymKeys.computeIfAbsent(name, n -> SecureId.generate());
    }
}
