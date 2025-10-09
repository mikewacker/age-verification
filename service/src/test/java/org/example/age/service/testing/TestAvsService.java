package org.example.age.service.testing;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.concurrent.CompletionStage;
import org.example.age.avs.api.AvsApi;
import org.example.age.common.api.VerificationRequest;
import org.example.age.common.api.crypto.SecureId;
import org.example.age.module.request.test.TestAccountId;
import org.example.age.testing.client.TestAsyncEndpoints;

/**
 * Decorator for {@link AvsApi} that converts uncaught exceptions to failed futures.
 * Also provides a way to set the account ID.
 */
@Singleton
public final class TestAvsService implements AvsApi {

    private final AvsApi delegate;
    private final TestAccountId accountId;

    @Inject
    TestAvsService(AvsApi delegate, TestAccountId accountId) {
        this.delegate = TestAsyncEndpoints.test(delegate, AvsApi.class);
        this.accountId = accountId;
    }

    @Override
    public CompletionStage<VerificationRequest> createVerificationRequestForSite(String siteId) {
        return delegate.createVerificationRequestForSite(siteId);
    }

    @Override
    public CompletionStage<Void> linkVerificationRequest(SecureId requestId) {
        return delegate.linkVerificationRequest(requestId);
    }

    @Override
    public CompletionStage<Void> sendAgeCertificate() {
        return delegate.sendAgeCertificate();
    }

    public void setAccountId(String accountId) {
        this.accountId.set(accountId);
    }
}
