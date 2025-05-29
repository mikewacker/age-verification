package org.example.age.service.testing;

import java.util.concurrent.CompletionStage;
import org.example.age.api.SignedAgeCertificate;
import org.example.age.api.SiteApi;
import org.example.age.api.VerificationRequest;
import org.example.age.api.VerificationState;
import org.example.age.common.testing.WebStageTesting;

/** Wrapper that converts uncaught exceptions to a failed stage. */
public record TestWrappedSiteService(SiteApi delegate) implements SiteApi {

    @Override
    public CompletionStage<VerificationState> getVerificationState() {
        return WebStageTesting.wrapExceptions(delegate::getVerificationState);
    }

    @Override
    public CompletionStage<VerificationRequest> createVerificationRequest() {
        return WebStageTesting.wrapExceptions(delegate::createVerificationRequest);
    }

    @Override
    public CompletionStage<Void> processAgeCertificate(SignedAgeCertificate signedAgeCertificate) {
        return WebStageTesting.wrapExceptions(() -> delegate.processAgeCertificate(signedAgeCertificate));
    }
}
