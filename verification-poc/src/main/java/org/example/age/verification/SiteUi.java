package org.example.age.verification;

import java.util.List;
import org.example.age.certificate.VerificationRequest;
import org.example.age.data.AgeRange;

/** Encapsulates user interactions with a social media site. */
public interface SiteUi {

    /** Gets a name for display purposes. */
    String getName();

    /** Determines if the user is verified. */
    boolean isVerified(String username);

    /** Gets the age range of a verified user. */
    AgeRange getAgeRange(String username);

    /** Gets the usernames of the guardians of a verified user, if the guardians have a verified account. */
    List<String> getGuardians(String username);

    /**
     * Creates a verification request for an account.
     *
     * <p>In the real workflow, the site opens the age verification service in a new window in the user's browser;
     * the URL includes the request ID.</p>
     *
     * <p>It would also be beneficial to let the user know when the verification request expires,
     * so the entire {@link VerificationRequest} object is returned.</p>
     */
    VerificationRequest createVerificationRequest(String username);
}
