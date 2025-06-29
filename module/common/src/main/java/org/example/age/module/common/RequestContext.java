package org.example.age.module.common;

import jakarta.ws.rs.core.HttpHeaders;

/**
 * Context for the HTTP request.
 * <p>
 * This context is only available in the thread that handles the HTTP request.
 */
public interface RequestContext {

    /** Gets the HTTP headers. */
    HttpHeaders httpHeaders();
}
