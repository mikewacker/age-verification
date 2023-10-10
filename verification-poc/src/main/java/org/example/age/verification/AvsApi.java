package org.example.age.verification;

import java.security.PublicKey;
import org.example.age.data.certificate.VerificationRequest;

/** API that encapsulates how a site interacts with the age verification service. */
public interface AvsApi {

    /**
     * Gets the public signing key.
     *
     * <p>In the real workflow, a digital certificate would verify that the age verification service owns this key.</p>
     */
    PublicKey getPublicSigningKey();

    /**
     * Generates a verification request that can be used to produce an age certificate for the site.
     *
     * <p>In the real workflow, this API method would ideally be authenticated on the site's side as well.
     * However, a verification request only has practical value once a site links it to an account.</p>
     */
    VerificationRequest generateVerificationRequestForSite(String siteId);
}
