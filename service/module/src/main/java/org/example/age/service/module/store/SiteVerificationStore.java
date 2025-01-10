package org.example.age.service.module.store;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import org.example.age.api.VerificationState;
import org.example.age.api.VerifiedUser;

/** Persistent store that retrieves the verification state for each account on a site. */
public interface SiteVerificationStore {

    /** Loads the {@link VerificationState} for the account. */
    CompletionStage<VerificationState> load(String accountId);

    /**
     * Saves a verified {@link VerificationState} for the account,
     * unless another account is verified with the same {@link VerifiedUser}.
     * Returns the account ID that is already verified if a conflict occurs.
     */
    CompletionStage<Optional<String>> trySave(String accountId, VerifiedUser user, OffsetDateTime expiration);
}
