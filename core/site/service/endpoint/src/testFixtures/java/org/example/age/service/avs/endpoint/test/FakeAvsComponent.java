package org.example.age.service.avs.endpoint.test;

import dagger.BindsInstance;
import dagger.Component;
import io.undertow.server.HttpHandler;
import javax.inject.Named;
import javax.inject.Singleton;
import org.example.age.testing.server.TestServer;

/** Component that creates a fake API {@link HttpHandler} for the age verification service. */
public final class FakeAvsComponent {

    /** Creates an API {@link HttpHandler}. */
    public static HttpHandler createApiHandler() {
        TestServer<?> siteServer = TestServer.get("site");
        UnderlyingComponent underlyingComponent =
                DaggerFakeAvsComponent_UnderlyingComponent.factory().create(siteServer);
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

        @Component.Factory
        interface Factory {

            UnderlyingComponent create(@BindsInstance @Named("site") TestServer<?> siteServer);
        }
    }
}
