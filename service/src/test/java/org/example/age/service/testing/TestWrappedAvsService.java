package org.example.age.service.testing;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.api.AuthMatchData;
import org.example.age.api.AvsApi;
import org.example.age.api.VerificationRequest;
import org.example.age.api.crypto.SecureId;

/** Wrapper that converts uncaught exceptions to failed futures. */
public record TestWrappedAvsService(AvsApi delegate) implements AvsApi {

    @Override
    public CompletionStage<VerificationRequest> createVerificationRequestForSite(
            String siteId, AuthMatchData authMatchData) {
        try {
            return delegate.createVerificationRequestForSite(siteId, authMatchData);
        } catch (RuntimeException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    public CompletionStage<Void> linkVerificationRequest(SecureId requestId) {
        try {
            return delegate.linkVerificationRequest(requestId);
        } catch (RuntimeException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    public CompletionStage<Void> sendAgeCertificate() {
        try {
            return delegate.sendAgeCertificate();
        } catch (RuntimeException e) {
            return CompletableFuture.failedFuture(e);
        }
    }
}
