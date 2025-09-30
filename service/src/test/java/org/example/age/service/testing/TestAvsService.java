package org.example.age.service.testing;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.util.concurrent.CompletionStage;
import org.example.age.api.AvsApi;
import org.example.age.api.VerificationRequest;
import org.example.age.api.crypto.SecureId;
import org.example.age.module.request.test.TestAccountId;
import org.example.age.testing.util.WebStageTesting;

/**
 * Decorator for {@link AvsApi} that converts uncaught exceptions to failed futures.
 * Also provides a way to set the account ID.
 */
@Singleton
public final class TestAvsService implements AvsApi {

    private final AvsApi delegate;
    private final TestAccountId accountId;

    @Inject
    TestAvsService(@Named("service") AvsApi delegate, TestAccountId accountId) {
        this.delegate = delegate;
        this.accountId = accountId;
    }

    @Override
    public CompletionStage<VerificationRequest> createVerificationRequestForSite(String siteId) {
        return WebStageTesting.wrapExceptions(() -> delegate.createVerificationRequestForSite(siteId));
    }

    @Override
    public CompletionStage<Void> linkVerificationRequest(SecureId requestId) {
        return WebStageTesting.wrapExceptions(() -> delegate.linkVerificationRequest(requestId));
    }

    @Override
    public CompletionStage<Void> sendAgeCertificate() {
        return WebStageTesting.wrapExceptions(delegate::sendAgeCertificate);
    }

    public void setAccountId(String accountId) {
        this.accountId.set(accountId);
    }
}
