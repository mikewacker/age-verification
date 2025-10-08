package org.example.age.module.crypto.test;

import dagger.Component;
import jakarta.inject.Singleton;
import java.util.function.Supplier;
import org.example.age.site.spi.SiteVerifiedUserLocalizer;
import org.example.age.testing.site.spi.SiteUserLocalizerTestTemplate;

public final class TestSiteVerifiedUserLocalizerTest extends SiteUserLocalizerTestTemplate {

    private static final SiteVerifiedUserLocalizer localizer = TestComponent.create();

    @Override
    protected SiteVerifiedUserLocalizer localizer() {
        return localizer;
    }

    /** Dagger component for {@link SiteVerifiedUserLocalizer}. */
    @Component(modules = TestSiteCryptoModule.class)
    @Singleton
    interface TestComponent extends Supplier<SiteVerifiedUserLocalizer> {

        static SiteVerifiedUserLocalizer create() {
            return DaggerTestSiteVerifiedUserLocalizerTest_TestComponent.create()
                    .get();
        }
    }
}
