package org.example.age.module.store.demo;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.api.VerifiedUser;
import org.example.age.service.module.store.AvsVerifiedUserStore;

/** Implementation of {@link AvsVerifiedUserStore}. Verified accounts are loaded from configuration. */
@Singleton
final class DemoAvsVerifiedUserStore implements AvsVerifiedUserStore {

    private final AvsStoresConfig config;

    @Inject
    public DemoAvsVerifiedUserStore(AvsStoresConfig config) {
        this.config = config;
    }

    @Override
    public CompletionStage<Optional<VerifiedUser>> tryLoad(String accountId) {
        Optional<VerifiedUser> maybeUser =
                Optional.ofNullable(config.verifiedAccounts().get(accountId));
        return CompletableFuture.completedFuture(maybeUser);
    }
}
