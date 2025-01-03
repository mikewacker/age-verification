package org.example.age.service;

import jakarta.ws.rs.container.ContainerRequestContext;

/** Gets the context for the HTTP request. Must be called in the thread that initially handles the HTTP request. */
@FunctionalInterface
public interface RequestContextProvider {

    ContainerRequestContext get();
}
