package org.example.age.service.testing;

import java.util.concurrent.CompletionStage;
import org.example.age.api.AuthMatchData;
import org.example.age.api.AvsApi;
import org.example.age.api.VerificationRequest;
import org.example.age.api.crypto.SecureId;
import org.example.age.common.testing.WebStageTesting;

/** Wrapper that converts uncaught exceptions to a failed stage. */
public record TestWrappedAvsService(AvsApi delegate) implements AvsApi {

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
