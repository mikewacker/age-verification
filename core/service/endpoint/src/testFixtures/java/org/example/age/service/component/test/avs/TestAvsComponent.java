package org.example.age.service.component.test.avs;

import dagger.Component;
import io.undertow.server.HttpHandler;
import javax.inject.Named;
import javax.inject.Singleton;

/** Component that creates a test API {@link HttpHandler} for the age verification service. */
public final class TestAvsComponent {

    /** Creates an API {@link HttpHandler}. */
    public static HttpHandler createApiHandler() {
        UnderlyingComponent component = DaggerTestAvsComponent_UnderlyingComponent.create();
        return component.apiHandler();
    }

    // static class
    private TestAvsComponent() {}

    /** Dagger component that provides an {@link HttpHandler}. */
    @Component(modules = TestAvsServiceModule.class)
    @Singleton
    interface UnderlyingComponent {

        @Named("api")
        HttpHandler apiHandler();
    }
}
