package org.example.age.service.testing;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.util.concurrent.CompletionStage;
import org.example.age.api.AvsApi;
import org.example.age.api.SignedAgeCertificate;
import org.example.age.api.SiteApi;
import org.example.age.api.VerificationRequest;
import org.example.age.api.VerificationState;
import org.example.age.common.testing.WebStageTesting;
import org.example.age.service.testing.request.TestAccountId;

/**
 * Test wrapper for {@link AvsApi}.
 * Converts uncaught exceptions to failed futures, and provides a way to set the account ID.
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
