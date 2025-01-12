package org.example.age.service.testing.request;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.NotAuthorizedException;
import java.util.Optional;
import org.example.age.service.module.request.AccountIdContext;

/** Fake implementation of {@link AccountIdContext}. */
@Singleton
final class FakeAccountIdContext implements AccountIdContext {

    private Optional<String> maybeAccountId = Optional.empty();

    @Inject
    public FakeAccountIdContext() {}

    @Override
    public String getForRequest() {
        return maybeAccountId.orElseThrow(() -> new NotAuthorizedException("failed to authenticate account"));
    }

    /** Sets the account ID. */
    public void set(String accountId) {
        maybeAccountId = Optional.of(accountId);
    }
}
