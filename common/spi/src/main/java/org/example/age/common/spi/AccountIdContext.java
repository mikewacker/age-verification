package org.example.age.common.spi;

/**
 * Gets the account ID from the HTTP request, or throws {@code NotAuthorizedException}.
 * <p>
 * This context is only available in the thread that handles the HTTP request.
 */
@FunctionalInterface
public interface AccountIdContext {

    String getForRequest();
}
