package org.example.age.service.endpoint.avs;

import java.time.Duration;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.Sender;
import org.example.age.api.def.avs.AvsApi;
import org.example.age.api.def.common.AuthMatchData;
import org.example.age.api.def.common.VerificationState;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.SecureId;

/** Stub service for {@link AvsApi}. */
@Singleton
final class StubAvsService implements AvsApi {

    @Inject
    public StubAvsService() {}

    @Override
    public void getVerificationState(Sender.Value<VerificationState> sender, String accountId, Dispatcher dispatcher) {
        VerificationState state = VerificationState.unverified();
        sender.sendValue(state);
    }

    @Override
    public void createVerificationSession(
            Sender.Value<VerificationSession> sender, String siteId, Dispatcher dispatcher) {
        VerificationRequest request = VerificationRequest.generateForSite(siteId, Duration.ofMinutes(5));
        VerificationSession session = VerificationSession.create(request);
        sender.sendValue(session);
    }

    @Override
    public void linkVerificationRequest(
            Sender.StatusCode sender, String accountId, SecureId requestId, Dispatcher dispatcher) {
        sender.sendOk();
    }

    @Override
    public void sendAgeCertificate(
            Sender.StatusCode sender, String accountId, AuthMatchData authData, Dispatcher dispatcher) {
        sender.sendOk();
    }
}
