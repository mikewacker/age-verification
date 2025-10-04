package org.example.age.module.request.test;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Optional;

/** Sets the account ID for testing. */
@Singleton
public final class TestAccountId {

    private Optional<String> maybeAccountId = Optional.empty();

    @Inject
    TestAccountId() {}

    /** Gets the account ID, or empty if it is not set. */
    Optional<String> tryGet() {
        return maybeAccountId;
    }

    /** Sets the account ID. */
    public void set(String accountId) {
        maybeAccountId = Optional.of(accountId);
    }
}
