package org.example.age.service.testing;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.api.crypto.SecureId;
import org.example.age.service.api.SiteLocalizationKeyStore;

/** Fake implementation of {@link SiteLocalizationKeyStore}. */
@Singleton
final class FakeSiteLocalizationKeyStore implements SiteLocalizationKeyStore {

    private final SecureId key = SecureId.generate();

    @Inject
    public FakeSiteLocalizationKeyStore() {}

    @Override
    public CompletionStage<SecureId> get() {
        return CompletableFuture.completedStage(key);
    }
}
