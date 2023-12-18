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
import org.example.age.service.location.common.Location;
import org.example.age.service.location.common.RefreshableAvsLocationProvider;
import org.example.age.service.verification.internal.site.FakeSiteVerificationProcessor;

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
            Sender.StatusCode sender, SignedAgeCertificate signedCertificate, Dispatcher dispatcher) {
        int statusCode = verificationProcessor.onAgeCertificateReceived(signedCertificate);
        sender.send(statusCode);
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
            sender.sendErrorCode(maybeSession.statusCode());
            return;
        }
        VerificationSession session = maybeSession.get();

        verificationProcessor.beginVerification(accountId);
        sender.sendValue(session.verificationRequest());
    }
}
