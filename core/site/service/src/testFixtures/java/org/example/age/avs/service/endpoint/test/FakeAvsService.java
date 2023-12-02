package org.example.age.avs.service.endpoint.test;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import org.example.age.api.Dispatcher;
import org.example.age.api.JsonSender;
import org.example.age.api.Sender;
import org.example.age.api.StatusCodeSender;
import org.example.age.avs.api.endpoint.AvsApi;
import org.example.age.avs.service.verification.internal.FakeAvsVerificationFactory;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.common.service.config.SiteLocation;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.SecureId;
import org.example.age.infra.service.client.RequestDispatcher;

/** Fake service for {@link AvsApi}. */
@Singleton
final class FakeAvsService implements AvsApi {

    private final FakeAvsVerificationFactory verificationFactory;
    private final RequestDispatcher requestDispatcher;
    private final Provider<SiteLocation> siteLocationProvider;

    private VerificationSession storedSession = null;
    private String storedAccountId = null;

    @Inject
    public FakeAvsService(
            FakeAvsVerificationFactory verificationFactory,
            RequestDispatcher requestDispatcher,
            Provider<SiteLocation> siteLocationProvider) {
        this.verificationFactory = verificationFactory;
        this.requestDispatcher = requestDispatcher;
        this.siteLocationProvider = siteLocationProvider;
    }

    @Override
    public void createVerificationSession(
            JsonSender<VerificationSession> sender, String siteId, Dispatcher dispatcher) {
        reset();
        storedSession = verificationFactory.createVerificationSession(siteId);
        sender.sendBody(storedSession);
    }

    @Override
    public void linkVerificationRequest(
            StatusCodeSender sender, String accountId, SecureId requestId, Dispatcher dispatcher) {
        if ((storedSession == null)
                || !requestId.equals(storedSession.verificationRequest().id())) {
            resetAndSendUserError(sender);
            return;
        }

        storedAccountId = accountId;
        sender.sendOk();
    }

    @Override
    public void sendAgeCertificate(
            StatusCodeSender sender, String accountId, AuthMatchData authData, Dispatcher dispatcher) {
        if (!accountId.equals(storedAccountId)) {
            resetAndSendUserError(sender);
            return;
        }

        SignedAgeCertificate signedCertificate =
                verificationFactory.createSignedAgeCertificate(accountId, authData, storedSession);
        reset();

        requestDispatcher
                .requestBuilder(sender, dispatcher)
                .post(getAgeCertificateUrl())
                .body(signedCertificate)
                .dispatchWithStatusCodeResponse(this::onAgeCertificateResponseReceived);
    }

    /** Gets the URL for the request to send a {@link SignedAgeCertificate} to a site. */
    private String getAgeCertificateUrl() {
        return siteLocationProvider.get().ageCertificateUrl();
    }

    /** Callback for the request to send a {@link SignedAgeCertificate} to a site. */
    private void onAgeCertificateResponseReceived(StatusCodeSender sender, int statusCode, Dispatcher dispatcher) {
        sender.send(statusCode);
    }

    /** Clears the stored verification data. */
    private void reset() {
        storedSession = null;
        storedAccountId = null;
    }

    /** Clears the stored verification data and sends a 418 error. */
    private void resetAndSendUserError(Sender sender) {
        reset();
        sender.sendErrorCode(418);
    }
}
