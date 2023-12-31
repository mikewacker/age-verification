package org.example.age.service.endpoint;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.mikewacker.drift.api.Dispatcher;
import io.github.mikewacker.drift.api.HttpOptional;
import io.github.mikewacker.drift.api.Sender;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.api.def.AuthMatchData;
import org.example.age.api.def.SiteApi;
import org.example.age.api.def.VerificationState;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.service.config.RefreshableSiteConfigProvider;
import org.example.age.service.infra.client.RequestDispatcher;
import org.example.age.service.location.Location;
import org.example.age.service.location.RefreshableAvsLocationProvider;
import org.example.age.service.verification.internal.SiteVerificationManager;

@Singleton
final class SiteService implements SiteApi {

    private final SiteVerificationManager verificationManager;
    private final RefreshableSiteConfigProvider siteConfigProvider;
    private final RefreshableAvsLocationProvider avsLocationProvider;
    private final RequestDispatcher requestDispatcher;

    @Inject
    public SiteService(
            SiteVerificationManager verificationManager,
            RefreshableSiteConfigProvider siteConfigProvider,
            RefreshableAvsLocationProvider avsLocationProvider,
            RequestDispatcher requestDispatcher) {
        this.verificationManager = verificationManager;
        this.siteConfigProvider = siteConfigProvider;
        this.avsLocationProvider = avsLocationProvider;
        this.requestDispatcher = requestDispatcher;
    }

    @Override
    public void getVerificationState(Sender.Value<VerificationState> sender, String accountId, Dispatcher dispatcher) {
        VerificationState state = verificationManager.getVerificationState(accountId);
        sender.sendValue(state);
    }

    @Override
    public void createVerificationRequest(
            Sender.Value<VerificationRequest> sender, String accountId, AuthMatchData authData, Dispatcher dispatcher) {
        requestDispatcher
                .requestBuilder(new TypeReference<VerificationSession>() {})
                .post(getVerificationSessionUrl())
                .build()
                .dispatch(sender, accountId, authData, dispatcher, this::handleVerificationSessionResponse);
    }

    @Override
    public void processAgeCertificate(
            Sender.Value<String> sender, SignedAgeCertificate signedCertificate, Dispatcher dispatcher) {
        HttpOptional<String> maybeRedirectPath = verificationManager.onAgeCertificateReceived(signedCertificate);
        sender.send(maybeRedirectPath);
    }

    /** Gets the URL for the request to get a {@link VerificationSession} from the age verification service. */
    private String getVerificationSessionUrl() {
        Location avsLocation = avsLocationProvider.getAvs();
        String siteId = siteConfigProvider.get().id();
        return avsLocation.apiUrl("/verification-session?site-id=%s", siteId);
    }

    /** Callback for the request to get a {@link VerificationSession} from the age verification service. */
    private void handleVerificationSessionResponse(
            Sender.Value<VerificationRequest> sender,
            String accountId,
            AuthMatchData authData,
            HttpOptional<VerificationSession> maybeSession,
            Dispatcher dispatcher) {
        if (maybeSession.isEmpty()) {
            int errorCode = mapVerificationSessionErrorCode(maybeSession.statusCode());
            sender.sendErrorCode(errorCode);
            return;
        }
        VerificationSession session = maybeSession.get();

        int statusCode = verificationManager.onVerificationSessionReceived(
                accountId, authData, session, dispatcher.getIoThread());
        if (statusCode != 200) {
            sender.sendErrorCode(statusCode);
            return;
        }

        VerificationRequest request = toRedirectUrl(session.verificationRequest());
        sender.sendValue(request);
    }

    /** Maps the backend error code to a frontend error code. */
    private static int mapVerificationSessionErrorCode(int backendErrorCode) {
        return (backendErrorCode / 100 == 5) ? 502 : 500;
    }

    /** Converts a redirect path in a {@link VerificationRequest} to a redirect URL. */
    private VerificationRequest toRedirectUrl(VerificationRequest request) {
        Location avsLocation = avsLocationProvider.getAvs();
        return request.convertRedirectPathToUrl(avsLocation.rootUrl());
    }
}
