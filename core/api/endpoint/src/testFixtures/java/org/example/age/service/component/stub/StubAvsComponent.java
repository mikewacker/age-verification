package org.example.age.service.component.stub;

import dagger.Component;
import io.undertow.server.HttpHandler;
import javax.inject.Named;
import javax.inject.Singleton;

/** Component that creates a stub API {@link HttpHandler} for the age verification service. */
public final class StubAvsComponent {

    /** Creates an API {@link HttpHandler}. */
    public static HttpHandler createApiHandler() {
        UnderlyingComponent component = DaggerStubAvsComponent_UnderlyingComponent.create();
        return component.apiHandler();
    }

    // static class
    private StubAvsComponent() {}

    /** Dagger component that provides an {@link HttpHandler}. */
    @Component(modules = TestStubAvsServiceModule.class)
    @Singleton
    interface UnderlyingComponent {

        @Named("api")
        HttpHandler apiHandler();
    }
}
