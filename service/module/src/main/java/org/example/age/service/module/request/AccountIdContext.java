package org.example.age.service.module.request;

import jakarta.ws.rs.NotAuthorizedException;
import java.util.concurrent.CompletionStage;

/**
 * Gets the account ID for the HTTP request, or throws {@link NotAuthorizedException}.
 * Must be called in the thread that initially handles the HTTP request.
 */
@FunctionalInterface
public interface AccountIdContext {

    CompletionStage<String> getForRequest();
}
