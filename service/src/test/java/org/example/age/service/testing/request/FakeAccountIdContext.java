package org.example.age.service.testing.request;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.NotAuthorizedException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.service.module.request.AccountIdContext;

/** Fake implementation of {@link AccountIdContext}. */
@Singleton
final class FakeAccountIdContext implements AccountIdContext {

    private String accountId = null;

    @Inject
    public FakeAccountIdContext() {}

    @Override
    public CompletionStage<String> getForRequest() {
        return (accountId != null)
                ? CompletableFuture.completedFuture(accountId)
                : CompletableFuture.failedFuture(new NotAuthorizedException("failed to authenticate account"));
    }

    /** Sets the account ID. */
    public void set(String accountId) {
        this.accountId = accountId;
    }
}
