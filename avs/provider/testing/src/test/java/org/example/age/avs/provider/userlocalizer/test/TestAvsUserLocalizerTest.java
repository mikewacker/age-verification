package org.example.age.avs.provider.userlocalizer.test;

import dagger.Component;
import jakarta.inject.Singleton;
import java.util.function.Supplier;
import org.example.age.avs.spi.AvsVerifiedUserLocalizer;
import org.example.age.testing.site.spi.AvsUserLocalizerTestTemplate;

public final class TestAvsUserLocalizerTest extends AvsUserLocalizerTestTemplate {

    private static final AvsVerifiedUserLocalizer localizer = TestComponent.create();

    @Override
    protected AvsVerifiedUserLocalizer localizer() {
        return localizer;
    }

    /** Dagger component for {@link AvsVerifiedUserLocalizer}. */
    @Component(modules = TestAvsUserLocalizerModule.class)
    @Singleton
    interface TestComponent extends Supplier<AvsVerifiedUserLocalizer> {

        static AvsVerifiedUserLocalizer create() {
            return DaggerTestAvsUserLocalizerTest_TestComponent.create().get();
        }
    }
}
