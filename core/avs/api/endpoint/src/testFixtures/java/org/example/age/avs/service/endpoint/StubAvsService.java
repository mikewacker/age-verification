package org.example.age.avs.service.endpoint;

import java.time.Duration;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.api.Dispatcher;
import org.example.age.api.JsonSender;
import org.example.age.api.StatusCodeSender;
import org.example.age.avs.api.endpoint.AvsApi;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.common.api.data.VerificationState;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.SecureId;

/** Stub service for {@link AvsApi}. */
@Singleton
public final class StubAvsService implements AvsApi {

    @Inject
    public StubAvsService() {}

    @Override
    public void getVerificationState(JsonSender<VerificationState> sender, String accountId, Dispatcher dispatcher) {
        VerificationState state = VerificationState.unverified();
        sender.sendValue(state);
    }

    @Override
    public void createVerificationSession(
            JsonSender<VerificationSession> sender, String siteId, Dispatcher dispatcher) {
        VerificationRequest request = VerificationRequest.generateForSite(siteId, Duration.ofMinutes(5));
        VerificationSession session = VerificationSession.create(request);
        sender.sendValue(session);
    }

    @Override
    public void linkVerificationRequest(
            StatusCodeSender sender, String accountId, SecureId requestId, Dispatcher dispatcher) {
        sender.sendOk();
    }

    @Override
    public void sendAgeCertificate(
            StatusCodeSender sender, String accountId, AuthMatchData authData, Dispatcher dispatcher) {
        sender.sendOk();
    }
}
