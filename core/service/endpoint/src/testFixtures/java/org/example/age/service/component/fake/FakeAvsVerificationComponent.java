package org.example.age.service.component.fake;

import dagger.Component;
import javax.inject.Singleton;
import org.example.age.service.verification.internal.FakeAvsVerificationFactory;

/** Component that creates a test {@link FakeAvsVerificationFactory}. */
public final class FakeAvsVerificationComponent {

    /** Creates a {@link FakeAvsVerificationFactory}. */
    public static FakeAvsVerificationFactory createFakeAvsVerificationFactory() {
        UnderlyingComponent component = DaggerFakeAvsVerificationComponent_UnderlyingComponent.create();
        return component.fakeAvsVerificationFactory();
    }

    // static class
    private FakeAvsVerificationComponent() {}

    /** Dagger component that provides a {@link FakeAvsVerificationFactory}. */
    @Component(modules = TestFakeAvsVerificationFactoryModule.class)
    @Singleton
    interface UnderlyingComponent {

        FakeAvsVerificationFactory fakeAvsVerificationFactory();
    }
}
