package org.example.age.api.site;

import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.Sender;
import org.example.age.api.common.AuthMatchData;
import org.example.age.api.common.VerificationState;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationSession;

/** Asynchronous API for a site. */
public interface SiteApi {

    /** Gets the {@link VerificationState} for an account. */
    void getVerificationState(Sender.Value<VerificationState> sender, String accountId, Dispatcher dispatcher)
            throws Exception;

    /** Creates a {@link VerificationSession} for the account. */
    void createVerificationSession(
            Sender.Value<VerificationSession> sender, String accountId, AuthMatchData authData, Dispatcher dispatcher)
            throws Exception;

    /** Processes a {@link SignedAgeCertificate} from the age verification service. */
    void processAgeCertificate(Sender.StatusCode sender, SignedAgeCertificate signedCertificate, Dispatcher dispatcher)
            throws Exception;
}
