package org.example.age.service.testing;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.api.VerificationState;
import org.example.age.api.VerificationStatus;
import org.example.age.service.api.SiteVerificationStore;

/**
 * Fake, in-memory implementation of {@link SiteVerificationStore}.
 * It does not check for duplicate verifications, though that result can be triggered if "duplicate" is the username.
 */
@Singleton
final class FakeSiteVerificationStore implements SiteVerificationStore {

    private static final VerificationState UNVERIFIED =
            VerificationState.builder().status(VerificationStatus.UNVERIFIED).build();

    private final Map<String, VerificationState> store = new HashMap<>();

    @Inject
    public FakeSiteVerificationStore() {}

    @Override
    public CompletionStage<VerificationState> load(String accountId) {
        VerificationState state = store.getOrDefault(accountId, UNVERIFIED);
        return CompletableFuture.completedFuture(state);
    }

    @Override
    public CompletionStage<Optional<String>> trySave(String accountId, VerificationState state) {
        if (accountId.equals("duplicate")) {
            return CompletableFuture.completedFuture(Optional.of("username"));
        }

        store.put(accountId, state);
        return CompletableFuture.completedFuture(Optional.empty());
    }

    @Override
    public CompletionStage<Void> delete(String accountId) {
        store.remove(accountId);
        return CompletableFuture.completedFuture(null);
    }
}
