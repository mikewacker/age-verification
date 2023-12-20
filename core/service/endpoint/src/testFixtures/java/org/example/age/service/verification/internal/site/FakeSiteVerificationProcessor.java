package org.example.age.service.verification.internal.site;

import org.example.age.api.base.HttpOptional;
import org.example.age.api.def.common.VerificationState;
import org.example.age.data.certificate.SignedAgeCertificate;

/** Processor that receives {@link SignedAgeCertificate}'s. */
public interface FakeSiteVerificationProcessor {

    /** Gets the {@link VerificationState} for an account. */
    VerificationState getVerificationState(String accountId);

    /** Begins the age verification process for an account. */
    void beginVerification(String accountId);

    /** Called when a {@link SignedAgeCertificate} is received, returning a redirect path. */
    HttpOptional<String> onAgeCertificateReceived(SignedAgeCertificate signedCertificate);
}
