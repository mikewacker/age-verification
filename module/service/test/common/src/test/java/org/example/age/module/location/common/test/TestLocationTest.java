package org.example.age.module.location.common.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dagger.Component;
import java.io.IOException;
import java.util.NoSuchElementException;
import javax.inject.Singleton;
import okhttp3.mockwebserver.MockResponse;
import org.example.age.service.location.common.Location;
import org.example.age.service.location.common.RefreshableAvsLocationProvider;
import org.example.age.service.location.common.RefreshableSiteLocationProvider;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.mock.MockServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class TestLocationTest {

    @RegisterExtension
    private static final MockServer siteServer = MockServer.register("site");

    @RegisterExtension
    private static final MockServer avsServer = MockServer.register("avs");

    private static RefreshableAvsLocationProvider avsLocationProvider;
    private static RefreshableSiteLocationProvider siteLocationProvider;

    @BeforeAll
    public static void createRefreshableLocationProviders() {
        avsLocationProvider = TestSiteComponent.createRefreshableAvsLocationProvider();
        siteLocationProvider = TestAvsComponent.createRefreshableSiteLocationProvider();
    }

    @Test
    public void siteRequestToAvs() throws IOException {
        avsServer.enqueue(new MockResponse());
        Location avsLocation = avsLocationProvider.getAvs();
        int statusCode = TestClient.requestBuilder().get(avsLocation.rootUrl()).execute();
        assertThat(statusCode).isEqualTo(200);
    }

    @Test
    public void avsRequestToSite() throws IOException {
        siteServer.enqueue(new MockResponse());
        Location siteLocation = siteLocationProvider.getSite("Site");
        int statusCode = TestClient.requestBuilder().get(siteLocation.rootUrl()).execute();
        assertThat(statusCode).isEqualTo(200);
    }

    @Test
    public void locationChanged() throws IOException {
        siteRequestToAvs();
    }

    @Test
    public void error_UnregisteredSite() {
        assertThatThrownBy(() -> siteLocationProvider.getSite("DNE")).isInstanceOf(NoSuchElementException.class);
    }

    /** Dagger component that provides a {@link RefreshableAvsLocationProvider}. */
    @Component(modules = TestAvsLocationModule.class)
    @Singleton
    interface TestSiteComponent {

        static RefreshableAvsLocationProvider createRefreshableAvsLocationProvider() {
            TestSiteComponent component = DaggerTestLocationTest_TestSiteComponent.create();
            return component.refreshableAvsLocationProvider();
        }

        RefreshableAvsLocationProvider refreshableAvsLocationProvider();
    }

    /** Dagger component that provides a {@link RefreshableSiteLocationProvider}. */
    @Component(modules = TestSiteLocationModule.class)
    @Singleton
    interface TestAvsComponent {

        static RefreshableSiteLocationProvider createRefreshableSiteLocationProvider() {
            TestAvsComponent component = DaggerTestLocationTest_TestAvsComponent.create();
            return component.refreshableSiteLocationProvider();
        }

        RefreshableSiteLocationProvider refreshableSiteLocationProvider();
    }
}
