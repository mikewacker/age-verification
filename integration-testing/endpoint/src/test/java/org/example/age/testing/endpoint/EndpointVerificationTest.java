package org.example.age.testing.endpoint;

import dagger.BindsInstance;
import dagger.Component;
import jakarta.inject.Singleton;
import java.time.Duration;
import java.util.Map;
import java.util.function.Supplier;
import org.example.age.avs.api.client.AvsApi;
import org.example.age.avs.endpoint.AvsEndpointConfig;
import org.example.age.avs.endpoint.AvsEndpointModule;
import org.example.age.avs.provider.accountstore.test.TestAvsAccountStoreModule;
import org.example.age.avs.provider.certificatesigner.test.TestCertificateSignerModule;
import org.example.age.avs.provider.userlocalizer.test.TestAvsUserLocalizerModule;
import org.example.age.common.api.AgeThresholds;
import org.example.age.common.provider.pendingstore.test.TestPendingStoreModule;
import org.example.age.common.spi.AccountIdContext;
import org.example.age.site.api.client.SiteApi;
import org.example.age.site.endpoint.SiteEndpointConfig;
import org.example.age.site.endpoint.SiteEndpointModule;
import org.example.age.site.provider.accountstore.test.TestSiteAccountStoreModule;
import org.example.age.site.provider.certificateverifier.test.TestCertificateVerifierModule;
import org.example.age.site.provider.userlocalizer.test.TestSiteUserLocalizerModule;
import org.example.age.testing.client.TestAsyncEndpoints;
import org.example.age.testing.integration.VerificationTestTemplate;
import org.junit.jupiter.api.BeforeEach;

public final class EndpointVerificationTest extends VerificationTestTemplate {

    private SiteApi siteClient;
    private AvsApi avsClient;

    private org.example.age.site.api.SiteApi siteEndpoint;
    private org.example.age.avs.api.AvsApi avsEndpoint;

    @BeforeEach
    public void createEndpoints() {
        siteClient =
                TestAsyncEndpoints.client(() -> siteEndpoint, org.example.age.site.api.SiteApi.class, SiteApi.class);
        avsClient = TestAsyncEndpoints.client(() -> avsEndpoint, org.example.age.avs.api.AvsApi.class, AvsApi.class);
        siteEndpoint = TestSiteComponent.create(avsClient);
        avsEndpoint = TestAvsComponent.create(siteClient);
    }

    @Override
    protected SiteApi siteClient() {
        return siteClient;
    }

    @Override
    protected AvsApi avsClient() {
        return avsClient;
    }

    /** Dagger component for the {@link org.example.age.site.api.SiteApi} endpoint. */
    @Component(
            modules = {
                SiteEndpointModule.class,
                TestSiteAccountStoreModule.class,
                TestPendingStoreModule.class,
                TestCertificateVerifierModule.class,
                TestSiteUserLocalizerModule.class,
            })
    @Singleton
    interface TestSiteComponent extends Supplier<org.example.age.site.api.SiteApi> {

        static org.example.age.site.api.SiteApi create(AvsApi avsClient) {
            AccountIdContext accountIdContext = () -> "username";
            SiteEndpointConfig config = SiteEndpointConfig.builder()
                    .id("site")
                    .verifiedAccountExpiresIn(Duration.ofDays(30))
                    .build();
            org.example.age.site.api.SiteApi endpoint = DaggerEndpointVerificationTest_TestSiteComponent.factory()
                    .create(accountIdContext, avsClient, config)
                    .get();
            return TestAsyncEndpoints.test(endpoint, org.example.age.site.api.SiteApi.class);
        }

        @Component.Factory
        interface Factory {

            TestSiteComponent create(
                    @BindsInstance AccountIdContext accountIdContext,
                    @BindsInstance AvsApi avsClient,
                    @BindsInstance SiteEndpointConfig config);
        }
    }

    /** Dagger component for the {@link org.example.age.avs.api.AvsApi} endpoint. */
    @Component(
            modules = {
                AvsEndpointModule.class,
                TestAvsAccountStoreModule.class,
                TestPendingStoreModule.class,
                TestCertificateSignerModule.class,
                TestAvsUserLocalizerModule.class,
            })
    @Singleton
    interface TestAvsComponent extends Supplier<org.example.age.avs.api.AvsApi> {

        static org.example.age.avs.api.AvsApi create(SiteApi siteClient) {
            AccountIdContext accountIdContext = () -> "person";
            Map<String, SiteApi> siteClients = Map.of("site", siteClient);
            AvsEndpointConfig config = AvsEndpointConfig.builder()
                    .verificationRequestExpiresIn(Duration.ofMinutes(5))
                    .putAgeThresholds("site", AgeThresholds.of(18))
                    .build();
            org.example.age.avs.api.AvsApi endpoint = DaggerEndpointVerificationTest_TestAvsComponent.factory()
                    .create(accountIdContext, siteClients, config)
                    .get();
            return TestAsyncEndpoints.test(endpoint, org.example.age.avs.api.AvsApi.class);
        }

        @Component.Factory
        interface Factory {

            TestAvsComponent create(
                    @BindsInstance AccountIdContext accountIdContext,
                    @BindsInstance Map<String, SiteApi> siteClients,
                    @BindsInstance AvsEndpointConfig config);
        }
    }
}
