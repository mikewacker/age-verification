package org.example.age.service.testing;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.util.concurrent.CompletionStage;
import org.example.age.api.AuthMatchData;
import org.example.age.api.AvsApi;
import org.example.age.api.VerificationRequest;
import org.example.age.api.crypto.SecureId;
import org.example.age.common.testing.WebStageTesting;

/** Wrapper that converts uncaught exceptions to a failed stage. */
@Singleton
final class TestWrappedAvsService implements AvsApi {

    private final AvsApi delegate;

    @Inject
    public TestWrappedAvsService(@Named("service") AvsApi delegate) {
        this.delegate = delegate;
    }

    @Override
    public CompletionStage<VerificationRequest> createVerificationRequestForSite(
            String siteId, AuthMatchData authMatchData) {
        return WebStageTesting.wrapExceptions(() -> delegate.createVerificationRequestForSite(siteId, authMatchData));
    }

    @Override
    public CompletionStage<Void> linkVerificationRequest(SecureId requestId) {
        return WebStageTesting.wrapExceptions(() -> delegate.linkVerificationRequest(requestId));
    }

    @Override
    public CompletionStage<Void> sendAgeCertificate() {
        return WebStageTesting.wrapExceptions(delegate::sendAgeCertificate);
    }
}
