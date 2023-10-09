package org.example.age.common.site.verification;

import java.util.Optional;

/**
 * Stores {@link VerificationState}'s for accounts.
 *
 * <p>The account ID could be a username, or it could be an ephemeral ID for a session.</p>
 */
public interface VerificationStore {

    /** Loads the {@link VerificationState} for the account. */
    VerificationState load(String accountId);

    /**
     * Saves the {@link VerificationState} for the account, unless a duplicate verification occurs.
     * Returns the account ID that is already verified if a duplicate verification occurs.
     */
    Optional<String> trySave(String accountId, VerificationState state);
}
