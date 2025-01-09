package org.example.age.module.request.demo;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.NotAuthorizedException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.service.api.request.AccountIdContext;
import org.example.age.service.api.request.RequestContextProvider;

/**
 * Implementation of {@link AccountIdContext}.
 * Uses an {@code Account-Id} header; it suffices to say that a production application should NOT do this.
 */
@Singleton
final class DemoAccountIdContext implements AccountIdContext {

    private final RequestContextProvider requestContextProvider;

    @Inject
    public DemoAccountIdContext(RequestContextProvider requestContextProvider) {
        this.requestContextProvider = requestContextProvider;
    }

    @Override
    public CompletionStage<String> getForRequest() {
        String accountId = requestContextProvider.get().getHeaderString("Account-Id");
        return (accountId != null)
                ? CompletableFuture.completedFuture(accountId)
                : CompletableFuture.failedFuture(new NotAuthorizedException("failed to authenticate account"));
    }
}
