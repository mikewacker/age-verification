package org.example.age.service.endpoint;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.mikewacker.drift.api.Dispatcher;
import io.github.mikewacker.drift.api.HttpOptional;
import io.github.mikewacker.drift.api.Sender;
import io.github.mikewacker.drift.backend.BackendDispatcher;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.api.def.AuthMatchData;
import org.example.age.api.def.AvsApi;
import org.example.age.api.def.VerificationState;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.SecureId;
import org.example.age.service.location.Location;
import org.example.age.service.location.RefreshableSiteLocationProvider;
import org.example.age.service.verification.internal.FakeAvsVerificationFactory;

/** Fake service for {@link AvsApi}. */
@Singleton
final class FakeAvsService implements AvsApi {

    private final FakeAvsVerificationFactory verificationFactory;
    private final RefreshableSiteLocationProvider siteLocationProvider;
    private final BackendDispatcher backendDispatcher;

    private VerificationSession storedSession = null;
    private String storedAccountId = null;

    @Inject
    public FakeAvsService(
            FakeAvsVerificationFactory verificationFactory,
            RefreshableSiteLocationProvider siteLocationProvider,
            BackendDispatcher backendDispatcher) {
        this.verificationFactory = verificationFactory;
        this.siteLocationProvider = siteLocationProvider;
        this.backendDispatcher = backendDispatcher;
    }

    @Override
    public void getVerificationState(Sender.Value<VerificationState> sender, String accountId, Dispatcher dispatcher) {
        VerificationState state = verificationFactory.getVerificationState(accountId);
        sender.sendValue(state);
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
            Sender.Value<String> sender, String accountId, AuthMatchData authData, Dispatcher dispatcher) {
        if (!accountId.equals(storedAccountId)) {
            resetAndSendUserError(sender);
            return;
        }

        SignedAgeCertificate signedCertificate =
                verificationFactory.createSignedAgeCertificate(accountId, authData, storedSession);
        reset();

        String siteId = signedCertificate.ageCertificate().verificationRequest().siteId();
        backendDispatcher
                .requestBuilder()
                .jsonResponse(new TypeReference<String>() {})
                .post(getAgeCertificateUrl(siteId))
                .body(signedCertificate)
                .build()
                .dispatch(sender, siteId, dispatcher, this::handleAgeCertificateResponse);
    }

    /** Gets the URL for the request to send a {@link SignedAgeCertificate} to a site. */
    private String getAgeCertificateUrl(String siteId) {
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
