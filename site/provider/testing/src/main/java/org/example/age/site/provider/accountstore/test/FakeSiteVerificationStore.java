package org.example.age.site.provider.accountstore.test;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.common.api.VerifiedUser;
import org.example.age.site.api.VerificationState;
import org.example.age.site.api.VerificationStatus;
import org.example.age.site.spi.SiteVerificationStore;

/**
 * Fake, in-memory implementation of {@link SiteVerificationStore}.
 * It does not check for duplicate or expired verifications,
 * though a duplicate verification can be triggered if the account ID is "duplicate".
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
    public CompletionStage<Optional<String>> trySave(String accountId, VerifiedUser user, OffsetDateTime expiration) {
        if (accountId.equals("duplicate")) {
            return CompletableFuture.completedFuture(Optional.of("username"));
        }

        VerificationState state = VerificationState.builder()
                .status(VerificationStatus.VERIFIED)
                .user(user)
                .expiration(expiration)
                .build();
        store.put(accountId, state);
        return CompletableFuture.completedFuture(Optional.empty());
    }
}
