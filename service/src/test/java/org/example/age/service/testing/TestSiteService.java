package org.example.age.service.testing;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.util.concurrent.CompletionStage;
import org.example.age.api.SignedAgeCertificate;
import org.example.age.api.SiteApi;
import org.example.age.api.VerificationRequest;
import org.example.age.api.VerificationState;
import org.example.age.module.request.test.TestAccountId;
import org.example.age.testing.util.WebStageTesting;

/**
 * Decorator for {@link SiteApi} that converts uncaught exceptions to failed futures.
 * Also provides a way to set the account ID.
 */
@Singleton
public final class TestSiteService implements SiteApi {

    private final SiteApi delegate;
    private final TestAccountId accountId;

    @Inject
    TestSiteService(@Named("service") SiteApi delegate, TestAccountId accountId) {
        this.delegate = delegate;
        this.accountId = accountId;
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

    public void setAccountId(String accountId) {
        this.accountId.set(accountId);
    }
}
