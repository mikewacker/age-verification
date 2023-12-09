package org.example.age.site.api.endpoint;

import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.StatusCodeSender;
import org.example.age.api.base.ValueSender;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.common.api.data.VerificationState;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationSession;

/** Asynchronous API for a site. */
public interface SiteApi {

    /** Gets the {@link VerificationState} for an account. */
    void getVerificationState(ValueSender<VerificationState> sender, String accountId, Dispatcher dispatcher);

    /** Creates a {@link VerificationSession} for the account. */
    void createVerificationSession(
            ValueSender<VerificationSession> sender, String accountId, AuthMatchData authData, Dispatcher dispatcher);

    /** Processes a {@link SignedAgeCertificate} from the age verification service. */
    void processAgeCertificate(StatusCodeSender sender, SignedAgeCertificate signedCertificate, Dispatcher dispatcher);
}
