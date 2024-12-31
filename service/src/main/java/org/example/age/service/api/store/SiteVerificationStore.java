package org.example.age.service.api.store;

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import org.example.age.api.VerificationState;

/** Persistent store that retrieves the verification state for each account on a site. */
public interface SiteVerificationStore {

    /** Loads the {@link VerificationState} for the account. */
    CompletionStage<VerificationState> load(String accountId);

    /**
     * Saves the {@link VerificationState} for the account, unless a duplicate verification occurs.
     * Returns the account ID that is already verified if a duplicate verification occurs.
     */
    CompletionStage<Optional<String>> trySave(String accountId, VerificationState state);

    /** Deletes the {@link VerificationState} for the account, making it unverified. */
    CompletionStage<Void> delete(String accountId);
}
