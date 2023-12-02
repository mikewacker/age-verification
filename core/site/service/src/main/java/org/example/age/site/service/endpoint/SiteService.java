package org.example.age.site.service.endpoint;

import com.fasterxml.jackson.core.type.TypeReference;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import org.example.age.api.Dispatcher;
import org.example.age.api.HttpOptional;
import org.example.age.api.JsonSender;
import org.example.age.api.StatusCodeSender;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.common.service.config.AvsLocation;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.infra.service.client.RequestDispatcher;
import org.example.age.infra.service.client.ResponseJsonCallback;
import org.example.age.site.api.endpoint.SiteApi;
import org.example.age.site.service.verification.internal.VerificationManager;

@Singleton
final class SiteService implements SiteApi {

    private final VerificationManager verificationManager;
    private final RequestDispatcher requestDispatcher;
    private final Provider<AvsLocation> avsLocationProvider;
    private final Provider<String> siteIdProvider;

    @Inject
    public SiteService(
            VerificationManager verificationManager,
            RequestDispatcher requestDispatcher,
            @Named("bridged") Provider<AvsLocation> avsLocationProvider,
            @Named("siteId") Provider<String> siteIdProvider) {
        this.verificationManager = verificationManager;
        this.requestDispatcher = requestDispatcher;
        this.avsLocationProvider = avsLocationProvider;
        this.siteIdProvider = siteIdProvider;
    }

    @Override
    public void createVerificationSession(
            JsonSender<VerificationSession> sender, String accountId, AuthMatchData authData, Dispatcher dispatcher) {
        String sessionUrl = avsLocationProvider.get().verificationSessionUrl(siteIdProvider.get());
        ResponseJsonCallback<JsonSender<VerificationSession>, VerificationSession> sessionCallback =
                new VerificationSessionCallback(verificationManager, accountId, authData);
        requestDispatcher
                .requestBuilder(sender, dispatcher)
                .post(sessionUrl)
                .dispatchWithJsonResponse(new TypeReference<>() {}, sessionCallback);
    }

    @Override
    public void processAgeCertificate(
            StatusCodeSender sender, SignedAgeCertificate signedCertificate, Dispatcher dispatcher) {
        int statusCode = verificationManager.onSignedAgeCertificateReceived(signedCertificate);
        sender.send(statusCode);
    }

    /**
     * Called when a response is received for the request
     * to get a {@link VerificationSession} from the age verification service.
     */
    private record VerificationSessionCallback(
            VerificationManager verificationManager, String accountId, AuthMatchData authData)
            implements ResponseJsonCallback<JsonSender<VerificationSession>, VerificationSession> {

        @Override
        public void onResponse(
                JsonSender<VerificationSession> sender,
                HttpOptional<VerificationSession> maybeSession,
                Dispatcher dispatcher) {
            if (maybeSession.isEmpty()) {
                int errorCode = (maybeSession.statusCode() / 100 == 5) ? 502 : 500;
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

            sender.sendBody(session);
        }
    }
}
