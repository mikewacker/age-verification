package org.example.age.common.avs.store;

import java.util.Optional;
import org.example.age.data.user.VerifiedUser;

/**
 * Persistent store of {@link VerifiedUser}'s for accounts.
 *
 * <p>Only one account can be verified for each {@link VerifiedUser}.</p>
 */
public interface VerifiedUserStore {

    /** Loads the {@link VerifiedUser} for the account, if present. */
    Optional<VerifiedUser> tryLoad(String accountId);

    /**
     * Saves the {@link VerifiedUser} for the account, unless another account has already been verified with said user.
     * Returns the account ID that is already verified if a duplicate verification occurs.
     */
    Optional<String> trySave(String accountId, VerifiedUser user);

    /** Deletes the {@link VerifiedUser} for the account. */
    void delete(String accountId);
}
