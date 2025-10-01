package org.example.age.service.module.store;

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import org.example.age.common.api.VerifiedUser;

/** Persistent store that retrieves the pseudonymous verified user linked to each person. */
public interface AvsVerifiedUserStore {

    /** Loads the {@link VerifiedUser} for the person, if present. */
    CompletionStage<Optional<VerifiedUser>> tryLoad(String accountId);
}
