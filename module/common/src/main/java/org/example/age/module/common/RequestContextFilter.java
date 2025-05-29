package org.example.age.module.common;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;

/** Implementation of {@link RequestContextProvider}. */
@Singleton
final class RequestContextFilter implements RequestContextProvider, ContainerRequestFilter {

    private final ThreadLocal<ContainerRequestContext> localRequestContext = new ThreadLocal<>();

    @Inject
    public RequestContextFilter() {}

    @Override
    public ContainerRequestContext get() {
        return localRequestContext.get();
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        localRequestContext.set(requestContext);
    }
}
