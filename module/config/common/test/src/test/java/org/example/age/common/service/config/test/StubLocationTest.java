package org.example.age.common.service.config.test;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import javax.inject.Provider;
import javax.inject.Singleton;
import org.example.age.common.service.config.AvsLocation;
import org.example.age.common.service.config.SiteLocation;
import org.example.age.data.crypto.SecureId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class StubLocationTest {

    private static Provider<AvsLocation> avsLocationProvider;
    private static Provider<SiteLocation> siteLocationProvider;

    @BeforeAll
    public static void createLocationProviders() {
        avsLocationProvider = TestAvsComponent.createAvsLocationProvider();
        siteLocationProvider = TestSiteComponent.createSiteLocationProvider();
    }

    @Test
    public void getLocations() {
        String avsUrl = avsLocationProvider.get().redirectUrl(SecureId.generate());
        assertThat(avsUrl).isNotNull();

        String siteUrl = siteLocationProvider.get().redirectUrl();
        assertThat(siteUrl).isNotNull();
    }

    @Component(modules = StubAvsLocationModule.class)
    @Singleton
    interface TestAvsComponent {

        static Provider<AvsLocation> createAvsLocationProvider() {
            TestAvsComponent component = DaggerStubLocationTest_TestAvsComponent.create();
            return component.avsLocationProvider();
        }

        Provider<AvsLocation> avsLocationProvider();
    }

    @Component(modules = StubSiteLocationModule.class)
    @Singleton
    interface TestSiteComponent {

        static Provider<SiteLocation> createSiteLocationProvider() {
            TestSiteComponent component = DaggerStubLocationTest_TestSiteComponent.create();
            return component.siteLocationProvider();
        }

        Provider<SiteLocation> siteLocationProvider();
    }
}
