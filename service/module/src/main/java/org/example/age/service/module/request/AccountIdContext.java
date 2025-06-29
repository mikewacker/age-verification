package org.example.age.service.module.request;

import jakarta.ws.rs.NotAuthorizedException;

/**
 * Gets the account ID from the HTTP request, or throws {@link NotAuthorizedException}.
 * <p>
 * This context is only available in the thread that handles the HTTP request.
 */
@FunctionalInterface
public interface AccountIdContext {

    String getForRequest();
}
