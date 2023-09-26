package org.example.age.common.verification;

import io.undertow.server.HttpServerExchange;
import java.util.Optional;
import org.example.age.data.VerifiedUser;

/**
 * Stores {@link VerifiedUser}'s that are linked to accounts.
 *
 * <p>The account ID could be a username, or it could be an ephemeral ID for a session.</p>
 */
public interface VerifiedUserStore {

    /** Tries to get the account ID from the {@link HttpServerExchange}. */
    Optional<String> tryGetAccountId(HttpServerExchange exchange);

    /** Tries to get the ID of the account that is linked to the {@link VerifiedUser}. */
    Optional<String> tryGetAccountId(VerifiedUser user);

    /** Loads the {@link VerificationState} for the account. */
    VerificationState load(String accountId);

    /** Saves the {@link VerificationState} for the account. */
    void save(String accountId, VerificationState state);
}
