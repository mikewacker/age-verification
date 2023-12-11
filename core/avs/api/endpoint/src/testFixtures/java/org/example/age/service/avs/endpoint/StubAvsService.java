package org.example.age.service.avs.endpoint;

import java.time.Duration;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.api.avs.AvsApi;
import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.StatusCodeSender;
import org.example.age.api.base.ValueSender;
import org.example.age.api.common.AuthMatchData;
import org.example.age.api.common.VerificationState;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.SecureId;

/** Stub service for {@link AvsApi}. */
@Singleton
final class StubAvsService implements AvsApi {

    @Inject
    public StubAvsService() {}

    @Override
    public void getVerificationState(ValueSender<VerificationState> sender, String accountId, Dispatcher dispatcher) {
        VerificationState state = VerificationState.unverified();
        sender.sendValue(state);
    }

    @Override
    public void createVerificationSession(
            ValueSender<VerificationSession> sender, String siteId, Dispatcher dispatcher) {
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
