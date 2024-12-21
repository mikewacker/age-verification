package org.example.age.service.endpoint;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.mikewacker.drift.api.Dispatcher;
import io.github.mikewacker.drift.api.HttpOptional;
import io.github.mikewacker.drift.api.Sender;
import io.github.mikewacker.drift.backend.BackendDispatcher;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.example.age.api.def.AuthMatchData;
import org.example.age.api.def.AvsApi;
import org.example.age.api.def.VerificationState;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.SecureId;
import org.example.age.service.location.Location;
import org.example.age.service.location.RefreshableSiteLocationProvider;
import org.example.age.service.verification.internal.AvsVerificationManager;

@Singleton
final class AvsService implements AvsApi {

    private final AvsVerificationManager verificationManager;
    private final RefreshableSiteLocationProvider siteLocationProvider;
    private final BackendDispatcher backendDispatcher;

    @Inject
    public AvsService(
            AvsVerificationManager verificationManager,
            RefreshableSiteLocationProvider siteLocationProvider,
            BackendDispatcher backendDispatcher) {
        this.verificationManager = verificationManager;
        this.siteLocationProvider = siteLocationProvider;
        this.backendDispatcher = backendDispatcher;
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
                verificationManager.createVerificationSession(siteId, dispatcher.getIoThread());
        sender.send(maybeSession);
    }

    @Override
    public void linkVerificationRequest(
            Sender.StatusCode sender, String accountId, SecureId requestId, Dispatcher dispatcher) {
        int statusCode = verificationManager.linkVerificationRequest(accountId, requestId, dispatcher.getIoThread());
        sender.send(statusCode);
    }

    @Override
    public void sendAgeCertificate(
            Sender.Value<String> sender, String accountId, AuthMatchData authData, Dispatcher dispatcher) {
        HttpOptional<SignedAgeCertificate> maybeSignedCertificate =
                verificationManager.createAgeCertificate(accountId, authData);
        if (maybeSignedCertificate.isEmpty()) {
            sender.sendErrorCode(maybeSignedCertificate);
            return;
        }
        SignedAgeCertificate signedCertificate = maybeSignedCertificate.get();

        String siteId = signedCertificate.ageCertificate().verificationRequest().siteId();
        backendDispatcher
                .requestBuilder()
                .jsonResponse(new TypeReference<String>() {})
                .post(getAgeCertificateUrl(signedCertificate))
                .body(signedCertificate)
                .build()
                .dispatch(sender, siteId, dispatcher, this::handleAgeCertificateResponse);
    }

    /** Gets the URL for the request to send a {@link SignedAgeCertificate} to a site. */
    private String getAgeCertificateUrl(SignedAgeCertificate signedCertificate) {
        String siteId = signedCertificate.ageCertificate().verificationRequest().siteId();
        Location siteLocation = siteLocationProvider.getSite(siteId);
        return siteLocation.apiUrl("/age-certificate/process");
    }

    /** Callback for the request to send a {@link SignedAgeCertificate} to a site. */
    private void handleAgeCertificateResponse(
            Sender.Value<String> sender, String siteId, HttpOptional<String> maybeRedirectPath, Dispatcher dispatcher) {
        if (maybeRedirectPath.isEmpty()) {
            sender.sendErrorCode(maybeRedirectPath);
            return;
        }
        String redirectPath = maybeRedirectPath.get();

        String redirectUrl = toRedirectUrl(redirectPath, siteId);
        sender.sendValue(redirectUrl);
    }

    /** Converts a redirect path to a redirect URL. */
    private String toRedirectUrl(String redirectPath, String siteId) {
        Location siteLocation = siteLocationProvider.getSite(siteId);
        return siteLocation.url(redirectPath);
    }
}
