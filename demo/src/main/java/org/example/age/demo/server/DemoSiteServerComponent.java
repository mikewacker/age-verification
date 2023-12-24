package org.example.age.demo.server;

import dagger.BindsInstance;
import dagger.Component;
import io.undertow.Undertow;
import javax.inject.Named;
import javax.inject.Singleton;

/** Component that creates a demo {@link Undertow} server for a site. */
public final class DemoSiteServerComponent {

    /** Creates an {@link Undertow} server. */
    public static Undertow createServer(String name) {
        UnderlyingComponent component =
                DaggerDemoSiteServerComponent_UnderlyingComponent.factory().create(name);
        return component.server();
    }

    // static class
    private DemoSiteServerComponent() {}

    /** Dagger component that provides an {@link Undertow} server. */
    @Component(modules = DemoSiteServerModule.class)
    @Singleton
    interface UnderlyingComponent {

        Undertow server();

        @Component.Factory
        interface Factory {

            UnderlyingComponent create(@BindsInstance @Named("name") String name);
        }
    }
}
