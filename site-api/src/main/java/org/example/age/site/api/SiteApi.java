package org.example.age.site.api;

import org.example.age.api.Dispatcher;
import org.example.age.api.JsonSender;
import org.example.age.api.StatusCodeSender;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationSession;

/** Asynchronous API for a site. */
public interface SiteApi {

    /** Creates a {@link VerificationSession} for the account. */
    void createVerificationSession(
            JsonSender<VerificationSession> sender, String accountId, AuthMatchData authData, Dispatcher dispatcher);

    /** Processes a {@link SignedAgeCertificate} from the age verification service. */
    void processAgeCertificate(StatusCodeSender sender, SignedAgeCertificate signedCertificate, Dispatcher dispatcher);
}
