package org.example.age.service.component.test;

import dagger.Component;
import jakarta.inject.Singleton;
import org.example.age.service.verification.internal.AvsVerificationManager;

/** Component that creates a test {@link AvsVerificationManager}. */
public final class TestAvsVerificationComponent {

    /** Creates an {@link AvsVerificationManager}. */
    public static AvsVerificationManager createAvsVerificationManager() {
        UnderlyingComponent component = DaggerTestAvsVerificationComponent_UnderlyingComponent.create();
        return component.avsVerificationManager();
    }

    // static class
    private TestAvsVerificationComponent() {}

    /** Dagger component that provides an {@link AvsVerificationManager}. */
    @Component(modules = TestAvsVerificationManagerModule.class)
    @Singleton
    interface UnderlyingComponent {

        AvsVerificationManager avsVerificationManager();
    }
}
