package org.example.age.module.common;

import jakarta.ws.rs.container.ContainerRequestContext;

/**
 * Gets the context for the HTTP request. Must be called in the thread that initially handles the HTTP request.
 * <p>
 * This provider must be registered with JAX-RS.
 */
@FunctionalInterface
public interface RequestContextProvider {

    ContainerRequestContext get();
}
