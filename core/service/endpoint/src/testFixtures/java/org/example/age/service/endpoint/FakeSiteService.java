package org.example.age.service.endpoint;

import com.fasterxml.jackson.core.type.TypeReference;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.base.Sender;
import org.example.age.api.def.AuthMatchData;
import org.example.age.api.def.SiteApi;
import org.example.age.api.def.VerificationState;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.service.infra.client.RequestDispatcher;
import org.example.age.service.location.Location;
import org.example.age.service.location.RefreshableAvsLocationProvider;
import org.example.age.service.verification.internal.FakeSiteVerificationProcessor;

@Singleton
final class FakeSiteService implements SiteApi {

    private final FakeSiteVerificationProcessor verificationProcessor;
    private final RefreshableAvsLocationProvider avsLocationProvider;
    private final RequestDispatcher requestDispatcher;

    @Inject
    public FakeSiteService(
            FakeSiteVerificationProcessor verificationProcessor,
            RefreshableAvsLocationProvider avsLocationProvider,
            RequestDispatcher requestDispatcher) {
        this.verificationProcessor = verificationProcessor;
        this.avsLocationProvider = avsLocationProvider;
        this.requestDispatcher = requestDispatcher;
    }

    @Override
    public void getVerificationState(Sender.Value<VerificationState> sender, String accountId, Dispatcher dispatcher) {
        VerificationState state = verificationProcessor.getVerificationState(accountId);
        sender.sendValue(state);
    }

    @Override
    public void createVerificationRequest(
            Sender.Value<VerificationRequest> sender, String accountId, AuthMatchData authData, Dispatcher dispatcher) {
        requestDispatcher
                .requestBuilder(dispatcher, new TypeReference<VerificationSession>() {})
                .post(getVerificationSessionUrl())
                .dispatch(sender, accountId, this::handleVerificationSessionResponse);
    }

    @Override
    public void processAgeCertificate(
            Sender.Value<String> sender, SignedAgeCertificate signedCertificate, Dispatcher dispatcher) {
        HttpOptional<String> maybeRedirectPath = verificationProcessor.onAgeCertificateReceived(signedCertificate);
        sender.send(maybeRedirectPath);
    }

    /** Gets the URL for the request to get a {@link VerificationSession} from the age verification service. */
    private String getVerificationSessionUrl() {
        Location avsLocation = avsLocationProvider.getAvs();
        return avsLocation.apiUrl("/verification-session?site-id=Site");
    }

    /** Callback for the request to get a {@link VerificationSession} from the age verification service. */
    private void handleVerificationSessionResponse(
            Sender.Value<VerificationRequest> sender,
            String accountId,
            HttpOptional<VerificationSession> maybeSession,
            Dispatcher dispatcher) {
        if (maybeSession.isEmpty()) {
            sender.sendErrorCode(maybeSession);
            return;
        }
        VerificationSession session = maybeSession.get();

        verificationProcessor.beginVerification(accountId);
        VerificationRequest request = toRedirectUrl(session.verificationRequest());
        sender.sendValue(request);
    }

    /** Converts a redirect path in a {@link VerificationRequest} to a redirect URL. */
    private VerificationRequest toRedirectUrl(VerificationRequest request) {
        Location avsLocation = avsLocationProvider.getAvs();
        return request.convertRedirectPathToUrl(avsLocation.rootUrl());
    }
}
