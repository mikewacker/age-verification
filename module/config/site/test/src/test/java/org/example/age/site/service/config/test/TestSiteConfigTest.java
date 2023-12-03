package org.example.age.site.service.config.test;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.BindsInstance;
import dagger.Component;
import java.io.IOException;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import okhttp3.mockwebserver.MockResponse;
import org.example.age.data.crypto.SecureId;
import org.example.age.site.service.config.SiteConfig;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.MockServer;
import org.example.age.testing.server.TestServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class TestSiteConfigTest {

    @RegisterExtension
    private static final MockServer avsServer = MockServer.create();

    private static Provider<SiteConfig> siteConfigProvider;

    @BeforeAll
    public static void createSiteConfigProvider() {
        siteConfigProvider = TestComponent.createSiteConfigProvider();
    }

    @Test
    public void exchange() throws IOException {
        avsServer.enqueue(new MockResponse());
        String avsUrl = siteConfigProvider.get().avsLocation().redirectUrl(SecureId.generate());
        int statusCode = TestClient.apiRequestBuilder().get(avsUrl).executeWithStatusCodeResponse();
        assertThat(statusCode).isEqualTo(200);
    }

    @Test
    public void exchange_DifferentTest() throws IOException {
        exchange();
    }

    @Component(modules = TestSiteConfigModule.class)
    @Singleton
    interface TestComponent {

        static Provider<SiteConfig> createSiteConfigProvider() {
            TestComponent component = DaggerTestSiteConfigTest_TestComponent.factory().create(avsServer);
            return component.siteConfigProvider();
        }

        Provider<SiteConfig> siteConfigProvider();

        @Component.Factory
        interface Factory {

            TestComponent create(@BindsInstance @Named("avs") TestServer<?> avsServer);
        }
    }
}
