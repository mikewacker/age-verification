package org.example.age.service.avs.endpoint.test;

import dagger.Component;
import io.undertow.server.HttpHandler;
import javax.inject.Named;
import javax.inject.Singleton;

/** Component that creates a fake API {@link HttpHandler} for the age verification service. */
public final class FakeAvsComponent {

    /** Creates an API {@link HttpHandler}. */
    public static HttpHandler createApiHandler() {
        UnderlyingComponent underlyingComponent = DaggerFakeAvsComponent_UnderlyingComponent.create();
        return underlyingComponent.apiHandler();
    }

    // static class
    private FakeAvsComponent() {}

    /** Dagger component that provides an {@link HttpHandler}. */
    @Component(modules = TestFakeAvsServiceModule.class)
    @Singleton
    interface UnderlyingComponent {

        @Named("api")
        HttpHandler apiHandler();
    }
}
