package org.example.age.service.module.request;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;

/** Implementation of {@link RequestContextProvider}. This provider must be registered with Jersey. */
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
