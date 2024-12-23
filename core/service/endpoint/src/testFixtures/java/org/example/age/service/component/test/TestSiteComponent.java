package org.example.age.service.component.test;

import dagger.Component;
import io.undertow.server.HttpHandler;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

/** Component that creates a test API {@link HttpHandler} for a site. */
public final class TestSiteComponent {

    /** Creates an API {@link HttpHandler}. */
    public static HttpHandler createApiHandler() {
        UnderlyingComponent underlyingComponent = DaggerTestSiteComponent_UnderlyingComponent.create();
        return underlyingComponent.apiHandler();
    }

    // static class
    private TestSiteComponent() {}

    /** Dagger component that provides an {@link HttpHandler}. */
    @Component(modules = TestSiteServiceModule.class)
    @Singleton
    interface UnderlyingComponent {

        @Named("api")
        HttpHandler apiHandler();
    }
}
