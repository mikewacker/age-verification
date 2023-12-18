package org.example.age.service.endpoint.site;

import java.time.Duration;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.Sender;
import org.example.age.api.def.common.AuthMatchData;
import org.example.age.api.def.common.VerificationState;
import org.example.age.api.def.site.SiteApi;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationRequest;

/** Stub service for {@link SiteApi}. */
@Singleton
final class StubSiteService implements SiteApi {

    @Inject
    public StubSiteService() {}

    @Override
    public void getVerificationState(Sender.Value<VerificationState> sender, String accountId, Dispatcher dispatcher) {
        VerificationState state = VerificationState.unverified();
        sender.sendValue(state);
    }

    @Override
    public void createVerificationRequest(
            Sender.Value<VerificationRequest> sender, String accountId, AuthMatchData authData, Dispatcher dispatcher) {
        VerificationRequest request = VerificationRequest.generateForSite("Site", Duration.ofMinutes(5), "");
        sender.sendValue(request);
    }

    @Override
    public void processAgeCertificate(
            Sender.Value<String> sender, SignedAgeCertificate signedCertificate, Dispatcher dispatcher) {
        sender.sendValue("");
    }
}
