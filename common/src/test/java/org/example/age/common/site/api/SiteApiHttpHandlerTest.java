package org.example.age.common.site.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.net.HostAndPort;
import dagger.Binds;
import dagger.BindsInstance;
import dagger.Component;
import dagger.Module;
import dagger.Provides;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;
import javax.inject.Named;
import javax.inject.Singleton;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.example.age.common.account.AccountIdExtractor;
import org.example.age.common.client.internal.RequestDispatcherModule;
import org.example.age.common.server.UndertowModule;
import org.example.age.common.site.api.testing.FakeAvsHandler;
import org.example.age.common.site.auth.UserAgentAuthMatchDataExtractorModule;
import org.example.age.common.site.config.SiteConfig;
import org.example.age.common.site.verification.InMemoryVerificationStoreModule;
import org.example.age.data.SecureId;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.testing.TestClient;
import org.example.age.testing.TestKeys;
import org.example.age.testing.TestUndertowServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class SiteApiHttpHandlerTest {

    @RegisterExtension
    private static final TestUndertowServer siteServer = TestUndertowServer.create(TestComponent::createServer);

    @RegisterExtension
    private static final TestUndertowServer fakeAvsServer = TestUndertowServer.create(FakeAvsComponent::createHandler);

    private static KeyPair avsSigningKeyPair;
    private static SecureId sitePseudonymKey;

    @BeforeAll
    public static void generateKeys() {
        avsSigningKeyPair = TestKeys.generateEd25519KeyPair();
        sitePseudonymKey = SecureId.generate();
    }

    @Test
    public void verify() throws IOException {
        String sessionUrl = siteServer.url("/api/verification-session");
        Request sessionRequest = createPostRequest(sessionUrl, "username1", "user agent");
        Response sessionResponse = TestClient.execute(sessionRequest);
        assertThat(sessionResponse.code()).isEqualTo(200);
        assertThat(sessionResponse.header("Content-Type")).isEqualTo("application/json");
        VerificationSession session =
                VerificationSession.deserialize(sessionResponse.body().bytes());
        assertThat(session.verificationRequest().siteId()).isEqualTo("Site");

        SecureId pseudonym = SecureId.generate();
        String certificateUrl = fakeAvsServer.url("/api/age-certificate?pseudonym=%s", pseudonym);
        Request certificateRequest = createPostRequest(certificateUrl, "", "user agent");
        Response certificateResponse = TestClient.execute(certificateRequest);
        assertThat(certificateResponse.code()).isEqualTo(200);
    }

    @Test
    public void failToVerify_VerifyError() throws IOException {
        String sessionUrl = siteServer.url("/api/verification-session");
        Request sessionRequest1 = createPostRequest(sessionUrl, "username2", "user agent");
        Response sessionResponse = TestClient.execute(sessionRequest1);
        assertThat(sessionResponse.code()).isEqualTo(200);

        SecureId pseudonym = SecureId.generate();
        String certificateUrl = fakeAvsServer.url("/api/age-certificate?pseudonym=%s", pseudonym);
        Request certificateRequest1 = createPostRequest(certificateUrl, "", "user agent");
        Response certificateResponse1 = TestClient.execute(certificateRequest1);
        assertThat(certificateResponse1.code()).isEqualTo(200);

        Request sessionRequest2 = createPostRequest(sessionUrl, "username3", "user agent");
        Response sessionResponse2 = TestClient.execute(sessionRequest2);
        assertThat(sessionResponse2.code()).isEqualTo(200);

        Request certificateRequest2 = createPostRequest(certificateUrl, "", "user agent");
        Response certificateResponse2 = TestClient.execute(certificateRequest2);
        assertThat(certificateResponse2.code()).isEqualTo(409);
    }

    @Test
    public void failToVerify_AuthError() throws IOException {
        String sessionUrl = siteServer.url("/api/verification-session");
        Request sessionRequest = createPostRequest(sessionUrl, "username4", "user agent 1");
        Response sessionResponse = TestClient.execute(sessionRequest);
        assertThat(sessionResponse.code()).isEqualTo(200);

        SecureId pseudonym = SecureId.generate();
        String certificateUrl = fakeAvsServer.url("/api/age-certificate?pseudonym=%s", pseudonym);
        Request certificateRequest = createPostRequest(certificateUrl, "", "user agent 2");
        Response certificateResponse = TestClient.execute(certificateRequest);
        assertThat(certificateResponse.code()).isEqualTo(401);
    }

    @Test
    public void failToVerify_AccountNotFound() throws IOException {
        String sessionUrl = siteServer.url("/api/verification-session");
        Request sessionRequest = createPostRequest(sessionUrl, "", "user agent");
        Response sessionResponse = TestClient.execute(sessionRequest);
        assertThat(sessionResponse.code()).isEqualTo(401);
    }

    @Test
    public void failToVerify_BadCertificate() throws IOException {
        String sessionUrl = fakeAvsServer.url("/api/verification-session?site-id=OtherSite");
        Request sessionRequest = createPostRequest(sessionUrl, "", "user agent");
        Response sessionResponse = TestClient.execute(sessionRequest);
        assertThat(sessionResponse.code()).isEqualTo(200);

        SecureId pseudonym = SecureId.generate();
        String certificateUrl = fakeAvsServer.url("/api/age-certificate?pseudonym=%s", pseudonym);
        Request certificateRequest = createPostRequest(certificateUrl, "", "user agent");
        Response certificateResponse = TestClient.execute(certificateRequest);
        assertThat(certificateResponse.code()).isEqualTo(400);
    }

    private static Request createPostRequest(String url, String accountId, String userAgent) {
        RequestBody emptyBody = RequestBody.create(new byte[0]);
        Request.Builder requestBuilder = new Request.Builder().url(url).post(emptyBody);
        if (!accountId.isEmpty()) {
            requestBuilder.header("Account-Id", accountId);
        }
        requestBuilder.header("User-Agent", userAgent);
        return requestBuilder.build();
    }

    private static Optional<String> extractAccountId(HttpServerExchange exchange) {
        return Optional.ofNullable(exchange.getRequestHeaders().getFirst("Account-Id"));
    }

    private static SiteConfig createSiteConfig() {
        return SiteConfig.builder()
                .avsHostAndPort(fakeAvsServer.hostAndPort())
                .avsPublicSigningKey(avsSigningKeyPair.getPublic())
                .siteId("Site")
                .pseudonymKey(sitePseudonymKey)
                .expiresIn(Duration.ofHours(1))
                .build();
    }

    private static void stubHandleHtmlRequest(HttpServerExchange exchange) {
        exchange.setStatusCode(StatusCodes.NOT_FOUND);
        exchange.endExchange();
    }

    /** Dagger module that binds dependencies needed to create a <code>@Named("api") {@link HttpHandler}</code>. */
    @Module(
            includes = {
                SiteApiModule.class,
                UserAgentAuthMatchDataExtractorModule.class,
                InMemoryVerificationStoreModule.class,
            })
    interface TestModule {

        @Provides
        @Singleton
        static AccountIdExtractor provideAccountIdExtractor() {
            return SiteApiHttpHandlerTest::extractAccountId;
        }

        @Provides
        @Singleton
        static Supplier<SiteConfig> provideSiteConfig() {
            return SiteApiHttpHandlerTest::createSiteConfig;
        }
    }

    /**
     * Dagger module that binds dependencies needed to create an {@link Undertow}.
     *
     * <p>Depends on an unbound <code>@Named("port") int</code>.</p>
     */
    @Module(includes = {UndertowModule.class, TestModule.class})
    interface TestUndertowModule {

        @Provides
        @Named("verifyHtml")
        @Singleton
        static HttpHandler provideVerifyHtmlHttpHandler() {
            return SiteApiHttpHandlerTest::stubHandleHtmlRequest;
        }

        @Provides
        @Singleton
        static Supplier<HostAndPort> provideHostAndPort(@Named("port") int port) {
            return () -> HostAndPort.fromParts("localhost", port);
        }
    }

    /** Dagger component that provides an {@link Undertow} server. */
    @Component(modules = TestUndertowModule.class)
    @Singleton
    interface TestComponent {

        static Undertow createServer(int port) {
            TestComponent component =
                    DaggerSiteApiHttpHandlerTest_TestComponent.factory().create(port);
            return component.server();
        }

        Undertow server();

        @Component.Factory
        interface Factory {

            TestComponent create(@BindsInstance @Named("port") int port);
        }
    }

    /**
     * Dagger module that publishes a binding for {@link HttpHandler},
     * which implements a fake age verification service.
     */
    @Module(includes = {UserAgentAuthMatchDataExtractorModule.class, RequestDispatcherModule.class})
    interface FakeAvsModule {

        @Binds
        HttpHandler bindHttpHandler(FakeAvsHandler impl);

        @Provides
        @Named("site")
        @Singleton
        static Supplier<HostAndPort> provideSiteHostAndPort() {
            return () -> siteServer.hostAndPort();
        }

        @Provides
        @Named("signing")
        @Singleton
        static Supplier<PrivateKey> providePrivateSigningKey() {
            return () -> avsSigningKeyPair.getPrivate();
        }
    }

    /** Dagger component that provides an {@link HttpHandler}. */
    @Component(modules = FakeAvsModule.class)
    @Singleton
    interface FakeAvsComponent {

        static HttpHandler createHandler() {
            FakeAvsComponent component = DaggerSiteApiHttpHandlerTest_FakeAvsComponent.create();
            return component.handler();
        }

        HttpHandler handler();
    }
}
