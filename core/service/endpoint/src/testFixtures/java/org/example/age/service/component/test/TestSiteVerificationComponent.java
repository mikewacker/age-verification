package org.example.age.service.component.test;

import dagger.Component;
import javax.inject.Singleton;
import org.example.age.service.verification.internal.SiteVerificationManager;

/** Component that creates a test {@link SiteVerificationManager}. */
public final class TestSiteVerificationComponent {

    /** Creates a {@link SiteVerificationManager}. */
    public static SiteVerificationManager createSiteVerificationManager() {
        UnderlyingComponent component = DaggerTestSiteVerificationComponent_UnderlyingComponent.create();
        return component.siteVerificationManager();
    }

    // static class
    private TestSiteVerificationComponent() {}

    /** Dagger component that provides a {@link SiteVerificationManager}. */
    @Component(modules = TestSiteVerificationManagerModule.class)
    @Singleton
    interface UnderlyingComponent {

        SiteVerificationManager siteVerificationManager();
    }
}
