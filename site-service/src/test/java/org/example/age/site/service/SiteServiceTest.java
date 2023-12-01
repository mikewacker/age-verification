package org.example.age.site.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import dagger.Component;
import dagger.Module;
import dagger.Provides;
import io.undertow.server.HttpHandler;
import java.io.IOException;
import java.security.PublicKey;
import java.time.Duration;
import java.util.Map;
import javax.inject.Named;
import javax.inject.Singleton;
import org.example.age.api.HttpOptional;
import org.example.age.common.api.extractor.builtin.DisabledAuthMatchDataExtractorModule;
import org.example.age.common.api.extractor.test.TestAccountIdExtractorModule;
import org.example.age.common.service.data.AvsLocation;
import org.example.age.common.service.store.InMemoryPendingStoreFactoryModule;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.SecureId;
import org.example.age.site.service.config.SiteConfig;
import org.example.age.site.service.store.InMemoryVerificationStoreModule;
import org.example.age.test.avs.service.FakeAvsServiceModule;
import org.example.age.test.common.service.crypto.TestSigningKeyModule;
import org.example.age.test.common.service.data.TestAvsLocationModule;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.TestServer;
import org.example.age.testing.server.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class SiteServiceTest {

    @RegisterExtension
    private static final TestUndertowServer siteServer =
            TestUndertowServer.fromHandlerAtPath(TestComponent::createHandler, "/api/");

    @RegisterExtension
    private static final TestUndertowServer fakeAvsServer =
            TestUndertowServer.fromHandlerAtPath(FakeAvsComponent::createHandler, "/api/");

    @Test
    public void verify() throws IOException {
        verify("publius-jr", "Billy Smith", 200);
    }

    @Test
    public void verifyFailed_DuplicateVerification() throws IOException {
        verify("publius", "John Smith", 200);
        verify("drop-table", "John Smith", 409);
    }

    private void verify(String siteAccountId, String avsAccountId, int expectedStatusCode) throws IOException {
        HttpOptional<VerificationSession> maybeSession = TestClient.apiRequestBuilder()
                .url(siteServer.url("/api/verification-session"))
                .headers(Map.of("Account-Id", siteAccountId))
                .post()
                .executeWithJsonResponse(new TypeReference<>() {});
        assertThat(maybeSession).isPresent();
        VerificationSession session = maybeSession.get();

        SecureId requestId = session.verificationRequest().id();
        int linkStatusCode = TestClient.apiRequestBuilder()
                .url(fakeAvsServer.url("/api/linked-verification-request?request-id=%s", requestId))
                .headers(Map.of("Account-Id", avsAccountId))
                .post()
                .executeWithStatusCodeResponse();
        assertThat(linkStatusCode).isEqualTo(200);

        int certificateStatusCode = TestClient.apiRequestBuilder()
                .url(fakeAvsServer.url("/api/age-certificate"))
                .headers(Map.of("Account-Id", avsAccountId))
                .post()
                .executeWithStatusCodeResponse();
        assertThat(certificateStatusCode).isEqualTo(expectedStatusCode);
    }

    private static SiteConfig createSiteConfig(AvsLocation location, PublicKey publicSigningKey) {
        return SiteConfig.builder()
                .avsLocation(location)
                .avsPublicSigningKey(publicSigningKey)
                .siteId("Site")
                .pseudonymKey(SecureId.generate())
                .expiresIn(Duration.ofDays(30))
                .build();
    }

    /** Dagger module that binds dependencies for <code>@Named("api") {@link HttpHandler}</code>. */
    @Module(
            includes = {
                SiteServiceModule.class,
                TestAccountIdExtractorModule.class,
                DisabledAuthMatchDataExtractorModule.class,
                InMemoryVerificationStoreModule.class,
                InMemoryPendingStoreFactoryModule.class,
                TestAvsLocationModule.class,
                TestSigningKeyModule.class,
            })
    interface TestModule {

        @Provides
        @Singleton
        static SiteConfig provideSiteConfig(AvsLocation location, @Named("signing") PublicKey publicSigningKey) {
            return createSiteConfig(location, publicSigningKey);
        }

        @Provides
        static TestServer<?> provideAvsServer() {
            return fakeAvsServer;
        }
    }

    /** Dagger components that provides an {@link HttpHandler}. */
    @Component(modules = TestModule.class)
    @Singleton
    interface TestComponent {

        static HttpHandler createHandler() {
            TestComponent component = DaggerSiteServiceTest_TestComponent.create();
            return component.handler();
        }

        @Named("api")
        HttpHandler handler();
    }

    /** Dagger module that binds dependencies for <code>@Named("api") {@link HttpHandler}</code>. */
    @Module(includes = FakeAvsServiceModule.class)
    interface FakeAvsModule {

        @Provides
        static TestServer<?> provideSiteServer() {
            return siteServer;
        }
    }

    /** Dagger components that provides an {@link HttpHandler}. */
    @Component(modules = FakeAvsModule.class)
    @Singleton
    interface FakeAvsComponent {

        static HttpHandler createHandler() {
            FakeAvsComponent component = DaggerSiteServiceTest_FakeAvsComponent.create();
            return component.handler();
        }

        @Named("api")
        HttpHandler handler();
    }
}
