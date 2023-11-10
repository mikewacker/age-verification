package org.example.age.site.api.testing;

import java.time.Duration;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.api.CodeSender;
import org.example.age.api.Dispatcher;
import org.example.age.api.JsonSender;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.site.api.SiteApi;

/** Stub service for {@link SiteApi}. */
@Singleton
public final class StubSiteService implements SiteApi {

    private static final String SITE_ID = "Site";
    private static final Duration EXPIRES_IN = Duration.ofMinutes(5);

    @Inject
    public StubSiteService() {}

    @Override
    public void createVerificationSession(
            JsonSender<VerificationSession> sender, String accountId, AuthMatchData authData, Dispatcher dispatcher) {
        VerificationRequest request = VerificationRequest.generateForSite(SITE_ID, EXPIRES_IN);
        VerificationSession session = VerificationSession.create(request);
        sender.sendBody(session);
    }

    @Override
    public void processAgeCertificate(
            CodeSender sender, SignedAgeCertificate signedCertificate, Dispatcher dispatcher) {
        sender.sendOk();
    }
}
