package org.example.age.common.service.config.test;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.BindsInstance;
import dagger.Component;
import java.io.IOException;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import okhttp3.mockwebserver.MockResponse;
import org.example.age.common.service.config.AvsLocation;
import org.example.age.common.service.config.SiteLocation;
import org.example.age.data.crypto.SecureId;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.TestServer;
import org.example.age.testing.server.mock.MockServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class TestLocationTest {

    @RegisterExtension
    private static final MockServer avsServer = MockServer.register("avs");

    @RegisterExtension
    private static final MockServer siteServer = MockServer.register("site");

    private static Provider<AvsLocation> avsLocationProvider;
    private static Provider<SiteLocation> siteLocationProvider;

    @BeforeAll
    public static void createLocationProviders() {
        avsLocationProvider = TestAvsComponent.createAvsLocationProvider();
        siteLocationProvider = TestSiteComponent.createSiteLocationProvider();
    }

    @Test
    public void exchange() throws IOException {
        avsServer.enqueue(new MockResponse());
        String avsUrl = avsLocationProvider.get().redirectUrl(SecureId.generate());
        int avsStatusCode = TestClient.apiRequestBuilder().get(avsUrl).executeWithStatusCodeResponse();
        assertThat(avsStatusCode).isEqualTo(200);

        siteServer.enqueue(new MockResponse());
        String siteUrl = siteLocationProvider.get().redirectUrl();
        int siteStatusCode = TestClient.apiRequestBuilder().get(siteUrl).executeWithStatusCodeResponse();
        assertThat(siteStatusCode).isEqualTo(200);
    }

    @Test
    public void exchange_DifferentTest() throws IOException {
        exchange();
    }

    @Component(modules = TestAvsLocationModule.class)
    @Singleton
    interface TestAvsComponent {

        static Provider<AvsLocation> createAvsLocationProvider() {
            TestAvsComponent component =
                    DaggerTestLocationTest_TestAvsComponent.factory().create(avsServer);
            return component.avsLocationProvider();
        }

        Provider<AvsLocation> avsLocationProvider();

        @Component.Factory
        interface Factory {

            TestAvsComponent create(@BindsInstance @Named("avs") TestServer<?> avsServer);
        }
    }

    @Component(modules = TestSiteLocationModule.class)
    @Singleton
    interface TestSiteComponent {

        static Provider<SiteLocation> createSiteLocationProvider() {
            TestSiteComponent component =
                    DaggerTestLocationTest_TestSiteComponent.factory().create(siteServer);
            return component.siteLocationProvider();
        }

        Provider<SiteLocation> siteLocationProvider();

        @Component.Factory
        interface Factory {

            TestSiteComponent create(@BindsInstance @Named("site") TestServer<?> siteServer);
        }
    }
}
