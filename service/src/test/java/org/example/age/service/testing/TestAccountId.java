package org.example.age.service.testing;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/** Sets the account ID for testing. Tests will fail if the account ID is not set. */
@Singleton
public final class TestAccountId {

    private final FakeAccountIdExtractor accountIdExtractor;

    @Inject
    TestAccountId(FakeAccountIdExtractor accountIdExtractor) {
        this.accountIdExtractor = accountIdExtractor;
    }

    /** Sets the account ID. */
    public void set(String accountId) {
        accountIdExtractor.set(accountId);
    }
}
