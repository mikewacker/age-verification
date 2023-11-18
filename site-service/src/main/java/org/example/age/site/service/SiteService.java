package org.example.age.site.service;

import com.fasterxml.jackson.core.type.TypeReference;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.example.age.api.CodeSender;
import org.example.age.api.Dispatcher;
import org.example.age.api.JsonSender;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.infra.service.client.RequestDispatcher;
import org.example.age.infra.service.client.ResponseBodyCallback;
import org.example.age.site.api.AvsLocation;
import org.example.age.site.api.SiteApi;
import org.example.age.site.service.verification.internal.VerificationManager;

@Singleton
final class SiteService implements SiteApi {

    private static RequestBody EMPTY_BODY = RequestBody.create(new byte[0]);

    private final VerificationManager verificationManager;
    private final RequestDispatcher requestDispatcher;
    private final Provider<AvsLocation> avsLocationProvider;
    private final Provider<String> siteIdProvider;

    @Inject
    public SiteService(
            VerificationManager verificationManager,
            RequestDispatcher requestDispatcher,
            Provider<AvsLocation> avsLocationProvider,
            @Named("siteId") Provider<String> siteIdProvider) {
        this.verificationManager = verificationManager;
        this.requestDispatcher = requestDispatcher;
        this.avsLocationProvider = avsLocationProvider;
        this.siteIdProvider = siteIdProvider;
    }

    @Override
    public void createVerificationSession(
            JsonSender<VerificationSession> sender, String accountId, AuthMatchData authData, Dispatcher dispatcher) {
        Request sessionRequest = createVerificationSessionRequest(siteIdProvider.get());
        ResponseBodyCallback<VerificationSession, JsonSender<VerificationSession>> sessionCallback =
                new VerificationSessionCallback(verificationManager, accountId, authData);
        requestDispatcher.dispatch(sessionRequest, new TypeReference<>() {}, sender, dispatcher, sessionCallback);
    }

    @Override
    public void processAgeCertificate(
            CodeSender sender, SignedAgeCertificate signedCertificate, Dispatcher dispatcher) {
        int statusCode = verificationManager.onSignedAgeCertificateReceived(signedCertificate);
        sender.send(statusCode);
    }

    /** Creates a request to get a {@link VerificationSession} from the age verification service. */
    private Request createVerificationSessionRequest(String siteId) {
        HttpUrl url = avsLocationProvider.get().verificationSessionUrl(siteId);
        return new Request.Builder().url(url).post(EMPTY_BODY).build();
    }

    /**
     * Called when a response is received for the request
     * to get a {@link VerificationSession} from the age verification service.
     */
    private record VerificationSessionCallback(
            VerificationManager verificationManager, String accountId, AuthMatchData authData)
            implements ResponseBodyCallback<VerificationSession, JsonSender<VerificationSession>> {

        @Override
        public void onResponse(
                Response response,
                VerificationSession session,
                JsonSender<VerificationSession> sender,
                Dispatcher dispatcher) {
            if (!response.isSuccessful()) {
                int errorCode = ((response.code() / 100) == 5) ? 502 : 500;
                sender.sendError(errorCode);
                return;
            }

            int statusCode =
                    verificationManager.onVerificationSessionReceived(accountId, authData, session, dispatcher);
            if (statusCode != 200) {
                sender.sendError(statusCode);
                return;
            }

            sender.sendBody(session);
        }
    }
}
