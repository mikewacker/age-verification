package org.example.age.service.component.fake.site;

import dagger.Component;
import io.undertow.server.HttpHandler;
import javax.inject.Named;
import javax.inject.Singleton;

/** Component that creates a fake API {@link HttpHandler} for a site. */
public final class FakeSiteComponent {

    /** Creates an API {@link HttpHandler}. */
    public static HttpHandler createApiHandler() {
        UnderlyingComponent component = DaggerFakeSiteComponent_UnderlyingComponent.create();
        return component.apiHandler();
    }

    // static class
    private FakeSiteComponent() {}

    /** Dagger component that provides an {@link HttpHandler}. */
    @Component(modules = TestFakeSiteServiceModule.class)
    @Singleton
    interface UnderlyingComponent {

        @Named("api")
        HttpHandler apiHandler();
    }
}
