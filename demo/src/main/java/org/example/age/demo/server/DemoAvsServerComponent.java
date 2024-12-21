package org.example.age.demo.server;

import dagger.BindsInstance;
import dagger.Component;
import io.undertow.Undertow;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

/** Component that creates a demo {@link Undertow} server for the age verification service. */
public final class DemoAvsServerComponent {

    /** Creates an {@link Undertow} server. */
    public static Undertow createServer(String name) {
        UnderlyingComponent component =
                DaggerDemoAvsServerComponent_UnderlyingComponent.factory().create(name);
        return component.server();
    }

    // static class
    private DemoAvsServerComponent() {}

    /** Dagger component that provides an {@link Undertow} server. */
    @Component(modules = DemoAvsServerModule.class)
    @Singleton
    interface UnderlyingComponent {

        Undertow server();

        @Component.Factory
        interface Factory {

            UnderlyingComponent create(@BindsInstance @Named("name") String name);
        }
    }
}
