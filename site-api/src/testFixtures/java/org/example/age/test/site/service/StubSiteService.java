package org.example.age.test.site.service;

import java.time.Duration;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.api.Dispatcher;
import org.example.age.api.JsonSender;
import org.example.age.api.StatusCodeSender;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.site.api.SiteApi;

/** Stub service for {@link SiteApi}. */
@Singleton
public final class StubSiteService implements SiteApi {

    @Inject
    public StubSiteService() {}

    @Override
    public void createVerificationSession(
            JsonSender<VerificationSession> sender, String accountId, AuthMatchData authData, Dispatcher dispatcher) {
        VerificationRequest request = VerificationRequest.generateForSite("Site", Duration.ofMinutes(5));
        VerificationSession session = VerificationSession.create(request);
        sender.sendBody(session);
    }

    @Override
    public void processAgeCertificate(
            StatusCodeSender sender, SignedAgeCertificate signedCertificate, Dispatcher dispatcher) {
        sender.sendOk();
    }
}
