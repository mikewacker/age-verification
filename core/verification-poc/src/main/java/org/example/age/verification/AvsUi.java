package org.example.age.verification;

import org.example.age.data.crypto.SecureId;

/** Encapsulates user interactions with the age verification service. */
public interface AvsUi {

    /** Gets a name for display purposes. */
    String getName();

    /**
     * Processes a request to verify an account for a specific site.
     *
     * <p>In the real workflow, a site will open the age verification service in a new window in the person's browser;
     * the request ID will be included in the URL. Once the person logs in to the age verification service,
     * the age verification service would then link the request ID to that person.
     * The age verification service would then confirm that the person wants to verify an account on the site.</p>
     */
    void processVerificationRequest(String realName, SecureId requestId);
}
