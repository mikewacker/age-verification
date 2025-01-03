package org.example.age.service.component.stub;

import dagger.Component;
import io.undertow.server.HttpHandler;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

/** Component that creates a stub API {@link HttpHandler} for a site. */
public final class StubSiteComponent {

    /** Creates an API {@link HttpHandler}. */
    public static HttpHandler createApiHandler() {
        UnderlyingComponent component = DaggerStubSiteComponent_UnderlyingComponent.create();
        return component.apiHandler();
    }

    // static class
    private StubSiteComponent() {}

    /** Dagger component that provides an {@link HttpHandler}. */
    @Component(modules = TestStubSiteServiceModule.class)
    @Singleton
    interface UnderlyingComponent {

        @Named("api")
        HttpHandler apiHandler();
    }
}
