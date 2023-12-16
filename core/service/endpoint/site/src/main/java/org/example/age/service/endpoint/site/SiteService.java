package org.example.age.service.endpoint.site;

import com.fasterxml.jackson.core.type.TypeReference;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.base.Sender;
import org.example.age.api.def.common.AuthMatchData;
import org.example.age.api.def.common.VerificationState;
import org.example.age.api.def.site.SiteApi;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.service.infra.client.RequestDispatcher;
import org.example.age.service.location.common.AvsLocation;
import org.example.age.service.module.config.site.RefreshableSiteConfigProvider;
import org.example.age.service.module.location.common.RefreshableAvsLocationProvider;
import org.example.age.service.verification.internal.site.SiteVerificationManager;

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
                .requestBuilder(dispatcher, new TypeReference<VerificationSession>() {})
                .post(getVerificationSessionUrl())
                .dispatch(sender, accountId, authData, this::handleVerificationSessionResponse);
    }

    @Override
    public void processAgeCertificate(
            Sender.StatusCode sender, SignedAgeCertificate signedCertificate, Dispatcher dispatcher) {
        int statusCode = verificationManager.onSignedAgeCertificateReceived(signedCertificate);
        sender.send(statusCode);
    }

    /** Gets the URL for the request to get a {@link VerificationSession} from the age verification service. */
    private String getVerificationSessionUrl() {
        AvsLocation avsLocation = avsLocationProvider.get();
        String siteId = siteConfigProvider.get().id();
        return avsLocation.verificationSessionUrl(siteId);
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

        int statusCode = verificationManager.onVerificationSessionReceived(accountId, authData, session, dispatcher);
        if (statusCode != 200) {
            sender.sendErrorCode(statusCode);
            return;
        }

        sender.sendValue(session.verificationRequest());
    }

    /** Maps the backend error code to a frontend error code. */
    private static int mapVerificationSessionErrorCode(int backendErrorCode) {
        return (backendErrorCode / 100 == 5) ? 502 : 500;
    }
}
