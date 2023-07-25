package org.example.age.verification;

import com.google.common.annotations.VisibleForTesting;
import org.example.age.data.VerifiedUser;

/** Site or service that stores users whose age and guardians (if applicable) are verified. */
@VisibleForTesting // and for demo
public interface VerifiedUserStore {

    /** Gets a name for display purposes. */
    String getName();

    /** Retrieves a verified user. */
    VerifiedUser retrieveVerifiedUser(String username);
}
