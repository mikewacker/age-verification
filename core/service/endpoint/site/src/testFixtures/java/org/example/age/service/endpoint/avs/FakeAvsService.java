package org.example.age.service.endpoint.avs;

import com.fasterxml.jackson.core.type.TypeReference;
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
        requestDispatcher
                .requestBuilder(dispatcher, new TypeReference<String>() {})
                .post(getAgeCertificateUrl(siteId))
                .body(signedCertificate)
                .dispatch(sender, siteId, this::handleAgeCertificateResponse);
    }

    /** Gets the URL for the request to send a {@link SignedAgeCertificate} to a site. */
    private String getAgeCertificateUrl(String siteId) {
        Location siteLocation = siteLocationProvider.getSite(siteId);
        return siteLocation.apiUrl("/age-certificate");
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
