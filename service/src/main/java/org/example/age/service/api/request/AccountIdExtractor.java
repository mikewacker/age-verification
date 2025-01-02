package org.example.age.service.api.request;

import jakarta.ws.rs.NotAuthorizedException;
import java.util.concurrent.CompletionStage;

/**
 * Extracts an account ID from an HTTP request, or throws {@link NotAuthorizedException}.
 * <p>
 * It should be called in the same thread that initially handles the HTTP request.
 */
@FunctionalInterface
public interface AccountIdExtractor {

    CompletionStage<String> getForRequest();
}
