package org.example.age.service.endpoint.avs;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.base.Sender;
import org.example.age.api.def.avs.AvsApi;
import org.example.age.api.def.common.AuthMatchData;
import org.example.age.api.def.common.VerificationState;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.SecureId;
import org.example.age.service.infra.client.RequestDispatcher;
import org.example.age.service.location.common.Location;
import org.example.age.service.location.common.RefreshableSiteLocationProvider;
import org.example.age.service.verification.internal.avs.AvsVerificationManager;

@Singleton
final class AvsService implements AvsApi {

    private final AvsVerificationManager verificationManager;
    private final RefreshableSiteLocationProvider siteLocationProvider;
    private final RequestDispatcher requestDispatcher;

    @Inject
    public AvsService(
            AvsVerificationManager verificationManager,
            RefreshableSiteLocationProvider siteLocationProvider,
            RequestDispatcher requestDispatcher) {
        this.verificationManager = verificationManager;
        this.siteLocationProvider = siteLocationProvider;
        this.requestDispatcher = requestDispatcher;
    }

    @Override
    public void getVerificationState(Sender.Value<VerificationState> sender, String accountId, Dispatcher dispatcher) {
        VerificationState state = verificationManager.getVerificationState(accountId);
        sender.sendValue(state);
    }

    @Override
    public void createVerificationSession(
            Sender.Value<VerificationSession> sender, String siteId, Dispatcher dispatcher) {
        HttpOptional<VerificationSession> maybeSession =
                verificationManager.createVerificationSession(siteId, dispatcher);
        sender.send(maybeSession);
    }

    @Override
    public void linkVerificationRequest(
            Sender.StatusCode sender, String accountId, SecureId requestId, Dispatcher dispatcher) {
        int statusCode = verificationManager.linkVerificationRequest(accountId, requestId, dispatcher);
        sender.send(statusCode);
    }

    @Override
    public void sendAgeCertificate(
            Sender.StatusCode sender, String accountId, AuthMatchData authData, Dispatcher dispatcher) {
        HttpOptional<SignedAgeCertificate> maybeSignedCertificate =
                verificationManager.createAgeCertificate(accountId, authData);
        if (maybeSignedCertificate.isEmpty()) {
            sender.sendErrorCode(maybeSignedCertificate.statusCode());
            return;
        }
        SignedAgeCertificate signedCertificate = maybeSignedCertificate.get();

        requestDispatcher
                .requestBuilder(dispatcher)
                .post(getAgeCertificateUrl(signedCertificate))
                .body(signedCertificate)
                .dispatch(sender, this::handleAgeCertificateResponse);
    }

    /** Gets the URL for the request to send a {@link SignedAgeCertificate} to a site. */
    private String getAgeCertificateUrl(SignedAgeCertificate signedCertificate) {
        String siteId = signedCertificate.ageCertificate().verificationRequest().siteId();
        Location siteLocation = siteLocationProvider.getSite(siteId);
        return siteLocation.apiUrl("/age-certificate");
    }

    /** Callback for the request to send a {@link SignedAgeCertificate} to a site. */
    private void handleAgeCertificateResponse(Sender.StatusCode sender, int statusCode, Dispatcher dispatcher) {
        sender.send(statusCode);
    }
}
