package org.example.age.module.location.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dagger.Component;
import java.util.NoSuchElementException;
import javax.inject.Singleton;
import org.example.age.service.location.Location;
import org.example.age.service.location.RefreshableAvsLocationProvider;
import org.example.age.service.location.RefreshableSiteLocationProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class ResourceLocationTest {

    private static RefreshableAvsLocationProvider avsLocationProvider;
    private static RefreshableSiteLocationProvider siteLocationProvider;

    @BeforeAll
    public static void createRefreshableLocationProviders() {
        avsLocationProvider = TestSiteComponent.createRefreshableAvsLocationProvider();
        siteLocationProvider = TestAvsComponent.createRefreshableSiteLocationProvider();
    }

    @Test
    public void getAvs() {
        Location avsLocation = avsLocationProvider.getAvs();
        assertThat(avsLocation.rootUrl()).isEqualTo("http://localhost:8090");
    }

    @Test
    public void getSite() {
        Location siteLocation = siteLocationProvider.getSite("Site");
        assertThat(siteLocation.rootUrl()).isEqualTo("http://localhost:8080");
    }

    @Test
    public void error_GetSite_UnregisteredSite() {
        assertThatThrownBy(() -> siteLocationProvider.getSite("DNE"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("DNE");
    }

    /** Dagger component that provides a {@link RefreshableAvsLocationProvider}. */
    @Component(modules = {ResourceAvsLocationModule.class, TestResourceModule.class})
    @Singleton
    interface TestSiteComponent {

        static RefreshableAvsLocationProvider createRefreshableAvsLocationProvider() {
            TestSiteComponent component = DaggerResourceLocationTest_TestSiteComponent.create();
            return component.refreshableAvsLocationProvider();
        }

        RefreshableAvsLocationProvider refreshableAvsLocationProvider();
    }

    /** Dagger component that provides a {@link RefreshableSiteLocationProvider}. */
    @Component(modules = {ResourceSiteLocationModule.class, TestResourceModule.class})
    @Singleton
    interface TestAvsComponent {

        static RefreshableSiteLocationProvider createRefreshableSiteLocationProvider() {
            TestAvsComponent component = DaggerResourceLocationTest_TestAvsComponent.create();
            return component.refreshableSiteLocationProvider();
        }

        RefreshableSiteLocationProvider refreshableSiteLocationProvider();
    }
}
