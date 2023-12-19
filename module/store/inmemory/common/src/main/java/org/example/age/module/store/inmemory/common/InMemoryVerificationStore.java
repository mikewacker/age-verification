package org.example.age.module.store.inmemory.common;

import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.api.def.common.VerificationState;
import org.example.age.api.def.common.VerificationStatus;
import org.example.age.data.crypto.SecureId;
import org.example.age.service.store.common.VerificationStore;

/** In-memory {@link VerificationStore}. */
@Singleton
final class InMemoryVerificationStore implements VerificationStore {

    private static final VerificationState UNVERIFIED = VerificationState.unverified();

    private final BiKeyMap<String, SecureId, VerificationState> store = BiKeyMap.create();

    @Inject
    public InMemoryVerificationStore(Optional<VerificationStoreInitializer> maybeInitializer) {
        maybeInitializer.ifPresent(initializer -> initializer.initialize(this));
    }

    @Override
    public VerificationState load(String accountId) {
        Optional<VerificationState> maybeState = store.tryGet(accountId);
        if (maybeState.isEmpty()) {
            return UNVERIFIED;
        }
        VerificationState state = maybeState.get();

        // Save if the state is updated.
        VerificationState updatedState = state.update();
        if (updatedState != state) {
            trySave(accountId, updatedState);
        }
        return updatedState;
    }

    @Override
    public Optional<String> trySave(String accountId, VerificationState state) {
        Optional<SecureId> maybePseudonym = state.status().equals(VerificationStatus.VERIFIED)
                ? Optional.of(state.verifiedUser().pseudonym())
                : Optional.empty();
        Optional<String> maybeConflictingAccountId = store.tryPut(accountId, maybePseudonym, state);
        if (maybeConflictingAccountId.isEmpty()) {
            return Optional.empty();
        }

        // Update the other account, in case it's expired.
        String conflictingAccountId = maybeConflictingAccountId.get();
        load(conflictingAccountId);
        return store.tryPut(accountId, maybePseudonym, state);
    }

    @Override
    public void delete(String accountId) {
        store.remove(accountId);
    }
}
