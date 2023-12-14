package org.example.age.module.location.common.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dagger.Component;
import java.io.IOException;
import java.util.NoSuchElementException;
import javax.inject.Singleton;
import okhttp3.mockwebserver.MockResponse;
import org.example.age.module.location.common.RefreshableSiteLocationProvider;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.mock.MockServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class TestSiteLocationTest {

    @RegisterExtension
    private static final MockServer siteServer = MockServer.register("site");

    private static RefreshableSiteLocationProvider siteLocationProvider;

    @BeforeAll
    public static void createRefreshableSiteLocationProvider() {
        siteLocationProvider = TestComponent.createRefreshableSiteLocationProvider();
    }

    @Test
    public void exchange() throws IOException {
        siteServer.enqueue(new MockResponse());
        String siteUrl = siteLocationProvider.get("Site").ageCertificateUrl();
        int statusCode = TestClient.requestBuilder().get(siteUrl).execute();
        assertThat(statusCode).isEqualTo(statusCode);
    }

    @Test
    public void exchange_DifferentLocation() throws IOException {
        exchange();
    }

    @Test
    public void error_UnregisteredSite() {
        assertThatThrownBy(() -> siteLocationProvider.get("DNE")).isInstanceOf(NoSuchElementException.class);
    }

    @Component(modules = TestSiteLocationModule.class)
    @Singleton
    interface TestComponent {

        static RefreshableSiteLocationProvider createRefreshableSiteLocationProvider() {
            TestComponent component = DaggerTestSiteLocationTest_TestComponent.create();
            return component.refreshableSiteLocationProvider();
        }

        RefreshableSiteLocationProvider refreshableSiteLocationProvider();
    }
}
