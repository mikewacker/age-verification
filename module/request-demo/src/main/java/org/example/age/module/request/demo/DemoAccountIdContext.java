package org.example.age.module.request.demo;

import io.github.mikewacker.darc.RequestContext;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.NotAuthorizedException;
import java.util.Optional;
import org.example.age.service.module.request.AccountIdContext;

/**
 * Implementation of {@link AccountIdContext}.
 * Uses an {@code Account-Id} header; it suffices to say that a production application should NOT do this.
 */
@Singleton
final class DemoAccountIdContext implements AccountIdContext {

    private final RequestContext requestContext;

    @Inject
    public DemoAccountIdContext(RequestContext requestContext) {
        this.requestContext = requestContext;
    }

    @Override
    public String getForRequest() {
        return Optional.ofNullable(requestContext.httpHeaders().getHeaderString("Account-Id"))
                .orElseThrow(() -> new NotAuthorizedException("failed to authenticate account"));
    }
}
