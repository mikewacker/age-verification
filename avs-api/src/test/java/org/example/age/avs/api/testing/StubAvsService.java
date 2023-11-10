package org.example.age.avs.api.testing;

import java.time.Duration;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.avs.api.AvsApi;
import org.example.age.common.api.CodeSender;
import org.example.age.common.api.ExchangeExecutors;
import org.example.age.common.api.JsonSender;
import org.example.age.common.api.data.auth.AuthMatchData;
import org.example.age.data.SecureId;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.data.certificate.VerificationSession;

/** Stub service for {@link AvsApi}. */
@Singleton
public final class StubAvsService implements AvsApi {

    private static final Duration EXPIRES_IN = Duration.ofMinutes(5);

    @Inject
    public StubAvsService() {}

    @Override
    public void createVerificationSession(
            JsonSender<VerificationSession> sender, String siteId, ExchangeExecutors executors) {
        VerificationRequest request = VerificationRequest.generateForSite(siteId, EXPIRES_IN);
        VerificationSession session = VerificationSession.create(request);
        sender.sendBody(session);
    }

    @Override
    public void linkVerificationRequest(
            CodeSender sender, String accountId, SecureId requestId, ExchangeExecutors executors) {
        sender.sendOk();
    }

    @Override
    public void sendAgeCertificate(
            CodeSender sender, String accountId, AuthMatchData authData, ExchangeExecutors executors) {
        sender.sendOk();
    }
}
