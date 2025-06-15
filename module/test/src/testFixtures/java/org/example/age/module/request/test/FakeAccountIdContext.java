package org.example.age.module.request.test;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.NotAuthorizedException;
import org.example.age.service.module.request.AccountIdContext;

/** Fake implementation of {@link AccountIdContext}. The account ID is set via {@link TestAccountId}. */
@Singleton
final class FakeAccountIdContext implements AccountIdContext {

    private final TestAccountId accountId;

    @Inject
    public FakeAccountIdContext(TestAccountId accountId) {
        this.accountId = accountId;
    }

    @Override
    public String getForRequest() {
        return accountId.tryGet().orElseThrow(() -> new NotAuthorizedException("failed to authenticate account"));
    }
}
