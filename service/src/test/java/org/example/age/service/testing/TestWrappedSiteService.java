package org.example.age.service.testing;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.util.concurrent.CompletionStage;
import org.example.age.api.SignedAgeCertificate;
import org.example.age.api.SiteApi;
import org.example.age.api.VerificationRequest;
import org.example.age.api.VerificationState;
import org.example.age.common.testing.WebStageTesting;

/** Wrapper that converts uncaught exceptions to a failed stage. */
@Singleton
final class TestWrappedSiteService implements SiteApi {

    private final SiteApi delegate;

    @Inject
    public TestWrappedSiteService(@Named("service") SiteApi delegate) {
        this.delegate = delegate;
    }

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
