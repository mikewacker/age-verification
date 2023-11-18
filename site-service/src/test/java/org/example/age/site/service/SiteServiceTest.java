package org.example.age.site.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import dagger.BindsInstance;
import dagger.Component;
import dagger.Module;
import dagger.Provides;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.time.Duration;
import java.util.Map;
import javax.inject.Named;
import javax.inject.Singleton;
import okhttp3.Response;
import org.example.age.avs.api.SiteLocation;
import org.example.age.common.service.data.DisabledAuthMatchDataExtractorModule;
import org.example.age.common.service.store.InMemoryPendingStoreFactoryModule;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.SecureId;
import org.example.age.data.crypto.SigningKeys;
import org.example.age.site.api.AvsLocation;
import org.example.age.site.service.config.SiteConfig;
import org.example.age.site.service.store.InMemoryVerificationStoreModule;
import org.example.age.site.service.test.FakeAvsServiceModule;
import org.example.age.test.server.undertow.TestUndertowModule;
import org.example.age.test.service.data.TestAccountIdExtractorModule;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.TestUndertowServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class SiteServiceTest {

    @RegisterExtension
    private static final TestUndertowServer siteServer = TestUndertowServer.create(TestComponent::createServer);

    @RegisterExtension
    private static final TestUndertowServer fakeAvsServer = TestUndertowServer.create(FakeAvsComponent::createServer);

    private static KeyPair avsSigningKeyPair;

    @BeforeAll
    public static void generateKeys() {
        avsSigningKeyPair = SigningKeys.generateEd25519KeyPair();
    }

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
        String sessionUrl = siteServer.url("/api/verification-session");
        Map<String, String> siteHeaders = Map.of("Account-Id", siteAccountId);
        Response sessionResponse = TestClient.post(sessionUrl, siteHeaders);
        assertThat(sessionResponse.code()).isEqualTo(200);
        VerificationSession session = TestClient.readBody(sessionResponse, new TypeReference<>() {});
        SecureId requestId = session.verificationRequest().id();

        String linkUrl = fakeAvsServer.url("/api/linked-verification-request?request-id=%s", requestId);
        Map<String, String> avsHeaders = Map.of("Account-Id", avsAccountId);
        Response linkResponse = TestClient.post(linkUrl, avsHeaders);
        assertThat(linkResponse.code()).isEqualTo(200);

        String certificateUrl = fakeAvsServer.url("/api/age-certificate");
        Response certificateResponse = TestClient.post(certificateUrl, avsHeaders);
        assertThat(certificateResponse.code()).isEqualTo(expectedStatusCode);
    }

    private static AvsLocation createAvsLocation() {
        return AvsLocation.builder(fakeAvsServer.hostAndPort()).redirectPath("").build();
    }

    private static SiteLocation createSiteLocation() {
        return SiteLocation.builder(siteServer.hostAndPort()).redirectPath("").build();
    }

    private static SiteConfig createSiteConfig() {
        return SiteConfig.builder()
                .avsLocation(createAvsLocation())
                .avsPublicSigningKey(avsSigningKeyPair.getPublic())
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
            })
    interface TestModule {

        @Provides
        @Singleton
        static SiteConfig provideSiteConfig() {
            return createSiteConfig();
        }
    }

    /** Dagger components that provides an {@link Undertow} server. */
    @Component(modules = {TestUndertowModule.class, TestModule.class})
    @Singleton
    interface TestComponent {

        static Undertow createServer(int port) {
            TestComponent component =
                    DaggerSiteServiceTest_TestComponent.factory().create(port);
            return component.server();
        }

        Undertow server();

        @Component.Factory
        interface Factory {

            TestComponent create(@BindsInstance @Named("port") int port);
        }
    }

    /** Dagger module that binds dependencies for <code>@Named("api") {@link HttpHandler}</code>. */
    @Module(includes = FakeAvsServiceModule.class)
    interface FakeAvsModule {

        @Provides
        @Singleton
        static SiteLocation provideAvsLocation() {
            return createSiteLocation();
        }

        @Provides
        @Named("signing")
        @Singleton
        static PrivateKey providePrivateSigningKey() {
            return avsSigningKeyPair.getPrivate();
        }
    }

    /** Dagger components that provides an {@link Undertow} server. */
    @Component(modules = {TestUndertowModule.class, FakeAvsModule.class})
    @Singleton
    interface FakeAvsComponent {

        static Undertow createServer(int port) {
            FakeAvsComponent component =
                    DaggerSiteServiceTest_FakeAvsComponent.factory().create(port);
            return component.server();
        }

        Undertow server();

        @Component.Factory
        interface Factory {

            FakeAvsComponent create(@BindsInstance @Named("port") int port);
        }
    }
}
