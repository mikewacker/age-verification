package org.example.age.service.endpoint;

import io.github.mikewacker.drift.api.Dispatcher;
import io.github.mikewacker.drift.api.Sender;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.time.Duration;
import org.example.age.api.def.AuthMatchData;
import org.example.age.api.def.AvsApi;
import org.example.age.api.def.VerificationState;
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
        VerificationRequest request = VerificationRequest.generateForSite(siteId, Duration.ofMinutes(5), "");
        VerificationSession session = VerificationSession.generate(request);
        sender.sendValue(session);
    }

    @Override
    public void linkVerificationRequest(
            Sender.StatusCode sender, String accountId, SecureId requestId, Dispatcher dispatcher) {
        sender.sendOk();
    }

    @Override
    public void sendAgeCertificate(
            Sender.Value<String> sender, String accountId, AuthMatchData authData, Dispatcher dispatcher) {
        sender.sendValue("");
    }
}
