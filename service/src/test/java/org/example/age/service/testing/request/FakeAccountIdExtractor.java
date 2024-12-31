package org.example.age.service.testing.request;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.example.age.service.api.request.AccountIdExtractor;

/** Fake implementation of {@link AccountIdExtractor}. The account ID must be set. */
@Singleton
final class FakeAccountIdExtractor implements AccountIdExtractor {

    private String accountId = null;

    @Inject
    public FakeAccountIdExtractor() {}

    @Override
    public String getForRequest() {
        if (accountId == null) {
            throw new IllegalStateException("account ID not set");
        }

        return accountId;
    }

    /** Sets the account ID. */
    public void set(String accountId) {
        this.accountId = accountId;
    }
}
