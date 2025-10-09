package org.example.age.service.testing;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.concurrent.CompletionStage;
import org.example.age.common.api.SignedAgeCertificate;
import org.example.age.common.api.VerificationRequest;
import org.example.age.module.request.test.TestAccountId;
import org.example.age.site.api.SiteApi;
import org.example.age.site.api.VerificationState;
import org.example.age.testing.client.TestAsyncEndpoints;

/**
 * Decorator for {@link SiteApi} that converts uncaught exceptions to failed futures.
 * Also provides a way to set the account ID.
 */
@Singleton
public final class TestSiteService implements SiteApi {

    private final SiteApi delegate;
    private final TestAccountId accountId;

    @Inject
    TestSiteService(SiteApi delegate, TestAccountId accountId) {
        this.delegate = TestAsyncEndpoints.test(delegate, SiteApi.class);
        this.accountId = accountId;
    }

    @Override
    public CompletionStage<VerificationState> getVerificationState() {
        return delegate.getVerificationState();
    }

    @Override
    public CompletionStage<VerificationRequest> createVerificationRequest() {
        return delegate.createVerificationRequest();
    }

    @Override
    public CompletionStage<Void> processAgeCertificate(SignedAgeCertificate signedAgeCertificate) {
        return delegate.processAgeCertificate(signedAgeCertificate);
    }

    public void setAccountId(String accountId) {
        this.accountId.set(accountId);
    }
}
