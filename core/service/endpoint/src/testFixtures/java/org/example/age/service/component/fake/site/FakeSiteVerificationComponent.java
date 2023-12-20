package org.example.age.service.component.fake.site;

import dagger.Component;
import javax.inject.Singleton;
import org.example.age.service.verification.internal.site.FakeSiteVerificationProcessor;

/** Component that creates a test {@link FakeSiteVerificationProcessor}. */
public final class FakeSiteVerificationComponent {

    /** Creates a {@link FakeSiteVerificationProcessor}. */
    public static FakeSiteVerificationProcessor createFakeSiteVerificationProcessor() {
        UnderlyingComponent component = DaggerFakeSiteVerificationComponent_UnderlyingComponent.create();
        return component.fakeSiteVerificationProcessor();
    }

    // static class
    private FakeSiteVerificationComponent() {}

    /** Dagger component that provides a {@link FakeSiteVerificationProcessor}. */
    @Component(modules = TestFakeSiteVerificationProcessorModule.class)
    @Singleton
    interface UnderlyingComponent {

        FakeSiteVerificationProcessor fakeSiteVerificationProcessor();
    }
}
