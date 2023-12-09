package org.example.age.avs.service.endpoint;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.Sender;
import org.example.age.api.base.StatusCodeSender;
import org.example.age.api.base.ValueSender;
import org.example.age.avs.api.endpoint.AvsApi;
import org.example.age.avs.service.verification.internal.FakeAvsVerificationFactory;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.common.api.data.VerificationState;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.SecureId;
import org.example.age.infra.service.client.RequestDispatcher;
import org.example.age.module.config.common.SiteLocation;

/** Fake service for {@link AvsApi}. */
@Singleton
final class FakeAvsService implements AvsApi {

    private final FakeAvsVerificationFactory verificationFactory;
    private final Provider<SiteLocation> siteLocationProvider;
    private final RequestDispatcher requestDispatcher;

    private VerificationSession storedSession = null;
    private String storedAccountId = null;

    @Inject
    public FakeAvsService(
            FakeAvsVerificationFactory verificationFactory,
            Provider<SiteLocation> siteLocationProvider,
            RequestDispatcher requestDispatcher) {
        this.verificationFactory = verificationFactory;
        this.siteLocationProvider = siteLocationProvider;
        this.requestDispatcher = requestDispatcher;
    }

    @Override
    public void getVerificationState(ValueSender<VerificationState> sender, String accountId, Dispatcher dispatcher) {
        sender.sendErrorCode(418);
    }

    @Override
    public void createVerificationSession(
            ValueSender<VerificationSession> sender, String siteId, Dispatcher dispatcher) {
        reset();
        storedSession = verificationFactory.createVerificationSession(siteId);
        sender.sendValue(storedSession);
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
