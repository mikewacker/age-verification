package org.example.age.site.service.endpoint;

import com.fasterxml.jackson.core.type.TypeReference;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import org.example.age.api.Dispatcher;
import org.example.age.api.HttpOptional;
import org.example.age.api.JsonSender;
import org.example.age.api.StatusCodeSender;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.common.api.data.VerificationState;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.infra.service.client.RequestDispatcher;
import org.example.age.infra.service.client.ResponseJsonCallback;
import org.example.age.module.config.common.AvsLocation;
import org.example.age.module.config.site.SiteConfig;
import org.example.age.site.api.endpoint.SiteApi;
import org.example.age.site.service.verification.internal.SiteVerificationManager;

@Singleton
final class SiteService implements SiteApi {

    private final SiteVerificationManager verificationManager;
    private final Provider<SiteConfig> siteConfigProvider;
    private final RequestDispatcher requestDispatcher;

    @Inject
    public SiteService(
            SiteVerificationManager verificationManager,
            Provider<SiteConfig> siteConfigProvider,
            RequestDispatcher requestDispatcher) {
        this.verificationManager = verificationManager;
        this.siteConfigProvider = siteConfigProvider;
        this.requestDispatcher = requestDispatcher;
    }

    @Override
    public void getVerificationState(JsonSender<VerificationState> sender, String accountId, Dispatcher dispatcher) {
        VerificationState state = verificationManager.getVerificationState(accountId);
        sender.sendValue(state);
    }

    @Override
    public void createVerificationSession(
            JsonSender<VerificationSession> sender, String accountId, AuthMatchData authData, Dispatcher dispatcher) {
        requestDispatcher
                .requestBuilder(sender, dispatcher)
                .post(getVerificationSessionUrl())
                .dispatchWithJsonResponse(
                        new TypeReference<>() {}, createVerificationSessionCallback(accountId, authData));
    }

    @Override
    public void processAgeCertificate(
            StatusCodeSender sender, SignedAgeCertificate signedCertificate, Dispatcher dispatcher) {
        int statusCode = verificationManager.onSignedAgeCertificateReceived(signedCertificate);
        sender.send(statusCode);
    }

    /** Gets the URL for the request to get a {@link VerificationSession} from the age verification service. */
    private String getVerificationSessionUrl() {
        AvsLocation avsLocation = siteConfigProvider.get().avsLocation();
        String siteId = siteConfigProvider.get().id();
        return avsLocation.verificationSessionUrl(siteId);
    }

    /** Creates a callback for the request to get a {@link VerificationSession} from the age verification service. */
    private VerificationSessionCallback createVerificationSessionCallback(String accountId, AuthMatchData authData) {
        return new VerificationSessionCallback(verificationManager, accountId, authData);
    }

    /** Callback for the request to get a {@link VerificationSession} from the age verification service. */
    private record VerificationSessionCallback(
            SiteVerificationManager verificationManager, String accountId, AuthMatchData authData)
            implements ResponseJsonCallback<JsonSender<VerificationSession>, VerificationSession> {

        @Override
        public void onResponse(
                JsonSender<VerificationSession> sender,
                HttpOptional<VerificationSession> maybeSession,
                Dispatcher dispatcher) {
            if (maybeSession.isEmpty()) {
                int errorCode = mapVerificationSessionErrorCode(maybeSession.statusCode());
                sender.sendErrorCode(errorCode);
                return;
            }
            VerificationSession session = maybeSession.get();

            int statusCode =
                    verificationManager.onVerificationSessionReceived(accountId, authData, session, dispatcher);
            if (statusCode != 200) {
                sender.sendErrorCode(statusCode);
                return;
            }

            sender.sendValue(session);
        }

        /** Maps the backend error code to a frontend error code. */
        private static int mapVerificationSessionErrorCode(int backendErrorCode) {
            return (backendErrorCode / 100 == 5) ? 502 : 500;
        }
    }
}
