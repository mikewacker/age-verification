package org.example.age.avs.spi;

import jakarta.ws.rs.ForbiddenException;
import java.util.concurrent.CompletionStage;

/** Persistent store that retrieves the pseudonymous user data for verified accounts. */
public interface AvsVerifiedAccountStore {

    /** Loads the pseudonymous user data for a verified account, or throws {@link ForbiddenException} */
    CompletionStage<VerifiedAccount> load(String accountId);
}
