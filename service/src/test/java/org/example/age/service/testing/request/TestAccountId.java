package org.example.age.service.testing.request;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/** Sets the account ID for testing. */
@Singleton
public final class TestAccountId {

    private final FakeAccountIdContext accountIdContext;

    @Inject
    TestAccountId(FakeAccountIdContext accountIdContext) {
        this.accountIdContext = accountIdContext;
    }

    /** Sets the account ID. */
    public void set(String accountId) {
        accountIdContext.set(accountId);
    }
}
