package org.example.age.service.testing;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.api.SignedAgeCertificate;
import org.example.age.api.SiteApi;
import org.example.age.api.VerificationRequest;
import org.example.age.api.VerificationState;

/** Wrapper that converts uncaught exceptions to failed futures. */
public record TestWrappedSiteService(SiteApi delegate) implements SiteApi {

    @Override
    public CompletionStage<VerificationState> getVerificationState() {
        try {
            return delegate.getVerificationState();
        } catch (RuntimeException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    public CompletionStage<VerificationRequest> createVerificationRequest() {
        try {
            return delegate.createVerificationRequest();
        } catch (RuntimeException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    public CompletionStage<Void> processAgeCertificate(SignedAgeCertificate signedAgeCertificate) {
        try {
            return delegate.processAgeCertificate(signedAgeCertificate);
        } catch (RuntimeException e) {
            return CompletableFuture.failedFuture(e);
        }
    }
}
