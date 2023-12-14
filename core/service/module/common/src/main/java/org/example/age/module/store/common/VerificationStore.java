package org.example.age.module.store.common;

import java.util.Optional;
import org.example.age.api.common.VerificationState;

/** Persistent store of {@link VerificationState}'s for accounts. */
public interface VerificationStore {

    /** Loads the {@link VerificationState} for the account. */
    VerificationState load(String accountId);

    /**
     * Saves the {@link VerificationState} for the account, unless a duplicate verification occurs.
     * Returns the account ID that is already verified if a duplicate verification occurs.
     */
    Optional<String> trySave(String accountId, VerificationState state);

    /** Deletes the {@link VerificationState} for the account. */
    void delete(String accountId);
}