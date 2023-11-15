package org.example.age.verification;

import org.example.age.data.certificate.SignedAgeCertificate;

/** API that encapsulates how the age verification services interacts with a site. */
public interface SiteApi {

    /**
     * Processes a signed age certificate.
     *
     * <p>In the real workflow, the signed certificate would be securely transmitted via TLS, ideally TLS 1.3.
     * TLS 1.3 provides forward secrecy.</p>
     */
    void processAgeCertificate(SignedAgeCertificate signedCertificate);
}
