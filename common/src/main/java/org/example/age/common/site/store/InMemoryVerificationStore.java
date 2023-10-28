package org.example.age.common.site.store;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.data.SecureId;
import org.example.age.data.VerifiedUser;

/** In-memory {@link VerificationStore}. */
@Singleton
final class InMemoryVerificationStore implements VerificationStore {

    private final Map<String, VerificationState> states = new HashMap<>();
    private final BiMap<SecureId, String> verifiedAccountIds = HashBiMap.create();

    private final Object lock = new Object();

    @Inject
    public InMemoryVerificationStore() {}

    @Override
    public VerificationState load(String accountId) {
        synchronized (lock) {
            Optional<VerificationState> maybeState = Optional.ofNullable(states.get(accountId));
            if (maybeState.isEmpty()) {
                return VerificationState.unverified();
            }

            VerificationState state = maybeState.get();
            VerificationState updatedState = state.update();
            if (updatedState != state) {
                save(accountId, updatedState);
            }

            return updatedState;
        }
    }

    @Override
    public Optional<String> trySave(String accountId, VerificationState state) {
        synchronized (lock) {
            Optional<String> maybeDuplicateAccountId = checkNoDuplicateVerifications(accountId, state);
            if (maybeDuplicateAccountId.isPresent()) {
                return maybeDuplicateAccountId;
            }

            save(accountId, state);
        }
        return Optional.empty();
    }

    @Override
    public void delete(String accountId) {
        synchronized (lock) {
            states.remove(accountId);
            verifiedAccountIds.inverse().remove(accountId);
        }
    }

    /** Checks that two accounts are not verified with the same {@link VerifiedUser}. */
    private Optional<String> checkNoDuplicateVerifications(String accountId, VerificationState state) {
        if (state.status() != VerificationStatus.VERIFIED) {
            return Optional.empty();
        }

        SecureId pseudonym = state.verifiedUser().pseudonym();
        Optional<String> maybeVerifiedAccountId = Optional.ofNullable(verifiedAccountIds.get(pseudonym));
        if (maybeVerifiedAccountId.isEmpty()) {
            return Optional.empty();
        }

        String verifiedAccountId = maybeVerifiedAccountId.get();
        if (verifiedAccountId.equals(accountId)) {
            return Optional.empty();
        }

        VerificationState otherState = load(verifiedAccountId);
        return (otherState.status() == VerificationStatus.VERIFIED) ? Optional.of(verifiedAccountId) : Optional.empty();
    }

    /** Saves the {@link VerificationState} for an account. */
    private void save(String accountId, VerificationState state) {
        states.put(accountId, state);
        if (state.status() != VerificationStatus.VERIFIED) {
            verifiedAccountIds.inverse().remove(accountId);
            return;
        }

        SecureId pseudonym = state.verifiedUser().pseudonym();
        verifiedAccountIds.put(pseudonym, accountId);
    }
}
