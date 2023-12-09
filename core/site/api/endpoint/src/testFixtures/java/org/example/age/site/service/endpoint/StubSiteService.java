package org.example.age.site.service.endpoint;

import java.time.Duration;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.StatusCodeSender;
import org.example.age.api.base.ValueSender;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.common.api.data.VerificationState;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.site.api.endpoint.SiteApi;

/** Stub service for {@link SiteApi}. */
@Singleton
public final class StubSiteService implements SiteApi {

    @Inject
    public StubSiteService() {}

    @Override
    public void getVerificationState(ValueSender<VerificationState> sender, String accountId, Dispatcher dispatcher) {
        VerificationState state = VerificationState.unverified();
        sender.sendValue(state);
    }

    @Override
    public void createVerificationSession(
            ValueSender<VerificationSession> sender, String accountId, AuthMatchData authData, Dispatcher dispatcher) {
        VerificationRequest request = VerificationRequest.generateForSite("Site", Duration.ofMinutes(5));
        VerificationSession session = VerificationSession.create(request);
        sender.sendValue(session);
    }

    @Override
    public void processAgeCertificate(
            StatusCodeSender sender, SignedAgeCertificate signedCertificate, Dispatcher dispatcher) {
        sender.sendOk();
    }
}
