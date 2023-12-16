package org.example.age.service.endpoint.avs;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.Sender;
import org.example.age.api.def.avs.AvsApi;
import org.example.age.api.def.common.AuthMatchData;
import org.example.age.api.def.common.VerificationState;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.SecureId;
import org.example.age.service.infra.client.RequestDispatcher;
import org.example.age.service.location.common.SiteLocation;
import org.example.age.service.module.location.common.RefreshableSiteLocationProvider;
import org.example.age.service.verification.internal.avs.FakeAvsVerificationFactory;

/** Fake service for {@link AvsApi}. */
@Singleton
final class FakeAvsService implements AvsApi {

    private final FakeAvsVerificationFactory verificationFactory;
    private final RefreshableSiteLocationProvider siteLocationProvider;
    private final RequestDispatcher requestDispatcher;

    private VerificationSession storedSession = null;
    private String storedAccountId = null;

    @Inject
    public FakeAvsService(
            FakeAvsVerificationFactory verificationFactory,
            RefreshableSiteLocationProvider siteLocationProvider,
            RequestDispatcher requestDispatcher) {
        this.verificationFactory = verificationFactory;
        this.siteLocationProvider = siteLocationProvider;
        this.requestDispatcher = requestDispatcher;
    }

    @Override
    public void getVerificationState(Sender.Value<VerificationState> sender, String accountId, Dispatcher dispatcher) {
        sender.sendErrorCode(418);
    }

    @Override
    public void createVerificationSession(
            Sender.Value<VerificationSession> sender, String siteId, Dispatcher dispatcher) {
        reset();
        storedSession = verificationFactory.createVerificationSession(siteId);
        sender.sendValue(storedSession);
    }

    @Override
    public void linkVerificationRequest(
            Sender.StatusCode sender, String accountId, SecureId requestId, Dispatcher dispatcher) {
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
            Sender.StatusCode sender, String accountId, AuthMatchData authData, Dispatcher dispatcher) {
        if (!accountId.equals(storedAccountId)) {
            resetAndSendUserError(sender);
            return;
        }

        String siteId = storedSession.verificationRequest().siteId();
        SignedAgeCertificate signedCertificate =
                verificationFactory.createSignedAgeCertificate(accountId, authData, storedSession);
        reset();

        requestDispatcher
                .requestBuilder(dispatcher)
                .post(getAgeCertificateUrl(siteId))
                .body(signedCertificate)
                .dispatch(sender, this::handleAgeCertificateResponse);
    }

    /** Gets the URL for the request to send a {@link SignedAgeCertificate} to a site. */
    private String getAgeCertificateUrl(String siteId) {
        SiteLocation siteLocation = siteLocationProvider.get(siteId);
        return siteLocation.ageCertificateUrl();
    }

    /** Callback for the request to send a {@link SignedAgeCertificate} to a site. */
    private void handleAgeCertificateResponse(Sender.StatusCode sender, int statusCode, Dispatcher dispatcher) {
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
