package org.example.age.service.endpoint;

import io.github.mikewacker.drift.api.Dispatcher;
import io.github.mikewacker.drift.api.Sender;
import java.time.Duration;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.api.def.AuthMatchData;
import org.example.age.api.def.SiteApi;
import org.example.age.api.def.VerificationState;
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
