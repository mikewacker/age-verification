package org.example.age.site.service;

import com.fasterxml.jackson.core.type.TypeReference;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.example.age.api.Dispatcher;
import org.example.age.api.JsonSender;
import org.example.age.api.StatusCodeSender;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.common.service.data.AvsLocation;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.infra.service.client.RequestDispatcher;
import org.example.age.infra.service.client.ResponseBodyCallback;
import org.example.age.site.api.SiteApi;
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
        HttpUrl sessionUrl = avsLocationProvider.get().verificationSessionUrl(siteIdProvider.get());
        ResponseBodyCallback<JsonSender<VerificationSession>, VerificationSession> sessionCallback =
                new VerificationSessionCallback(verificationManager, accountId, authData);
        requestDispatcher
                .createExchangeBuilder(sessionUrl, sender, dispatcher)
                .post()
                .dispatchWithResponseBody(new TypeReference<>() {}, sessionCallback);
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
            implements ResponseBodyCallback<JsonSender<VerificationSession>, VerificationSession> {

        @Override
        public void onResponse(
                JsonSender<VerificationSession> sender,
                Response response,
                VerificationSession session,
                Dispatcher dispatcher) {
            if (!response.isSuccessful()) {
                int errorCode = ((response.code() / 100) == 5) ? 502 : 500;
                sender.sendErrorCode(errorCode);
                return;
            }

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
