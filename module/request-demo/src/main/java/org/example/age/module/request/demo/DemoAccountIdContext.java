package org.example.age.module.request.demo;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.container.ContainerRequestContext;
import java.util.Optional;
import org.example.age.module.common.RequestContextProvider;
import org.example.age.service.module.request.AccountIdContext;

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
    public String getForRequest() {
        ContainerRequestContext requestContext = requestContextProvider.get();
        return Optional.ofNullable(requestContext.getHeaderString("Account-Id"))
                .orElseThrow(() -> new NotAuthorizedException("failed to authenticate account"));
    }
}
