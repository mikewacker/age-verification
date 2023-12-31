package org.example.age.api.def;

import io.github.mikewacker.drift.api.Dispatcher;
import io.github.mikewacker.drift.api.Sender;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationRequest;

/** Asynchronous API for a site. */
public interface SiteApi {

    /** Gets the {@link VerificationState} for an account. */
    void getVerificationState(Sender.Value<VerificationState> sender, String accountId, Dispatcher dispatcher)
            throws Exception;

    /** Creates a {@link VerificationRequest} for an account. */
    void createVerificationRequest(
            Sender.Value<VerificationRequest> sender, String accountId, AuthMatchData authData, Dispatcher dispatcher)
            throws Exception;

    /**
     * Processes a {@link SignedAgeCertificate} from the age verification service.
     *
     * <p>Sends back a redirect path.</p>
     */
    void processAgeCertificate(
            Sender.Value<String> sender, SignedAgeCertificate signedCertificate, Dispatcher dispatcher)
            throws Exception;
}
