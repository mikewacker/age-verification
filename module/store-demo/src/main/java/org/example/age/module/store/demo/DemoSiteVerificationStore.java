package org.example.age.module.store.demo;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.api.VerificationState;
import org.example.age.api.VerificationStatus;
import org.example.age.api.VerifiedUser;
import org.example.age.api.crypto.SecureId;
import org.example.age.service.module.store.SiteVerificationStore;

/** Implementation of {@link SiteVerificationStore}. Data is not persisted. */
@Singleton
final class DemoSiteVerificationStore implements SiteVerificationStore {

    private static final VerificationState UNVERIFIED =
            VerificationState.builder().status(VerificationStatus.UNVERIFIED).build();

    private final Map<String, VerificationState> states = new HashMap<>();
    private final BiMap<SecureId, String> verifiedAccounts = HashBiMap.create();
    private final Object lock = new Object();

    @Inject
    public DemoSiteVerificationStore() {}

    @Override
    public CompletionStage<VerificationState> load(String accountId) {
        synchronized (lock) {
            VerificationState state = loadAndUpdate(accountId);
            return CompletableFuture.completedFuture(state);
        }
    }

    @Override
    public CompletionStage<Optional<String>> trySave(String accountId, VerifiedUser user, OffsetDateTime expiration) {
        synchronized (lock) {
            Optional<String> maybeConflictingAccountId = Optional.ofNullable(verifiedAccounts.get(user.getPseudonym()));
            if (maybeConflictingAccountId.isPresent()) {
                String conflictingAccountId = maybeConflictingAccountId.get();
                if (!conflictingAccountId.equals(accountId)) {
                    VerificationState conflictingState = loadAndUpdate(conflictingAccountId);
                    if (conflictingState.getStatus() == VerificationStatus.VERIFIED) {
                        return CompletableFuture.completedFuture(Optional.of(conflictingAccountId));
                    }
                }
            }

            VerificationState state = VerificationState.builder()
                    .status(VerificationStatus.VERIFIED)
                    .user(user)
                    .expiration(expiration)
                    .build();
            save(accountId, state);
            return CompletableFuture.completedFuture(Optional.empty());
        }
    }

    /** Loads the {@link VerificationState} and updates it if necessary. */
    private VerificationState loadAndUpdate(String accountId) {
        VerificationState state = states.getOrDefault(accountId, UNVERIFIED);
        if ((state.getStatus() == VerificationStatus.VERIFIED)
                && state.getExpiration().isBefore(OffsetDateTime.now(ZoneOffset.UTC))) {
            state = VerificationState.builder()
                    .status(VerificationStatus.EXPIRED)
                    .expiration(state.getExpiration())
                    .build();
            save(accountId, state);
        }
        return state;
    }

    /** Saves the {@link VerificationState}. Assumes no conflicts exist. */
    private void save(String accountId, VerificationState state) {
        states.put(accountId, state);
        if (state.getStatus() == VerificationStatus.VERIFIED) {
            verifiedAccounts.put(state.getUser().getPseudonym(), accountId);
        } else {
            verifiedAccounts.inverse().remove(accountId);
        }
    }
}
