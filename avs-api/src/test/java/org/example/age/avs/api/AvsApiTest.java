package org.example.age.avs.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Binds;
import dagger.BindsInstance;
import dagger.Component;
import dagger.Module;
import dagger.Provides;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import java.io.IOException;
import java.util.Map;
import javax.inject.Named;
import javax.inject.Singleton;
import okhttp3.Response;
import org.example.age.avs.api.testing.StubAvsService;
import org.example.age.common.service.data.UserAgentAuthMatchDataExtractorModule;
import org.example.age.data.DataMapper;
import org.example.age.data.SecureId;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.TestUndertowServer;
import org.example.age.testing.server.undertow.TestUndertowModule;
import org.example.age.testing.service.data.TestAccountIdExtractorModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class AvsApiTest {

    @RegisterExtension
    private static final TestUndertowServer avsServer = TestUndertowServer.create(TestComponent::createServer);

    @Test
    public void verify() throws IOException {
        String sessionUrl = avsServer.url("/api/verification-session?site-id=Site");
        Response sessionResponse = TestClient.post(sessionUrl);
        assertThat(sessionResponse.code()).isEqualTo(200);
        VerificationSession session = TestClient.readBody(sessionResponse, new TypeReference<>() {});
        assertThat(session.verificationRequest().siteId()).isEqualTo("Site");

        SecureId requestId = session.verificationRequest().id();
        String linkUrl = avsServer.url("/api/linked-verification-request?request-id=%s", requestId);
        Map<String, String> userHeaders = Map.of("Account-Id", "username", "User-Agent", "agent");
        Response linkResponse = TestClient.post(linkUrl, userHeaders);
        assertThat(linkResponse.code()).isEqualTo(200);

        String certificateUrl = avsServer.url("/api/age-certificate");
        Response certificateResponse = TestClient.post(certificateUrl, userHeaders);
        assertThat(certificateResponse.code()).isEqualTo(200);
    }

    @Test
    public void error_MissingAccountId() throws IOException {
        SecureId requestId = SecureId.generate();
        String linkUrl = avsServer.url("/api/linked-verification-request?request-id=%s", requestId);
        Map<String, String> userHeaders = Map.of("User-Agent", "agent");
        Response linkResponse = TestClient.post(linkUrl, userHeaders);
        assertThat(linkResponse.code()).isEqualTo(401);

        String certificateUrl = avsServer.url("/api/age-certificate");
        Response certificateResponse = TestClient.post(certificateUrl, userHeaders);
        assertThat(certificateResponse.code()).isEqualTo(401);
    }

    @Test
    public void error_MissingSiteId() throws IOException {
        String sessionUrl = avsServer.url("/api/verification-session");
        Response sessionResponse = TestClient.post(sessionUrl);
        assertThat(sessionResponse.code()).isEqualTo(400);
    }

    @Test
    public void error_MissingRequestId() throws IOException {
        String linkUrl = avsServer.url("/api/linked-verification-request");
        Map<String, String> userHeaders = Map.of("Account-Id", "username", "User-Agent", "agent");
        Response linkResponse = TestClient.post(linkUrl, userHeaders);
        assertThat(linkResponse.code()).isEqualTo(400);
    }

    @Test
    public void error_BadPath() throws IOException {
        String url = avsServer.url("/api/does-not-exist");
        Response response = TestClient.get(url);
        assertThat(response.code()).isEqualTo(404);
    }

    /** Dagger module that binds dependencies needed to create a <code>@Named("api") {@link HttpHandler}</code>. */
    @Module(
            includes = {
                AvsApiModule.class,
                UserAgentAuthMatchDataExtractorModule.class,
                TestAccountIdExtractorModule.class
            })
    interface TestModule {

        @Binds
        AvsApi bindAvsApi(StubAvsService impl);

        @Provides
        @Singleton
        static ObjectMapper provideObjectMapper() {
            return DataMapper.get();
        }
    }

    /** Dagger component that provides an {@link Undertow} server. */
    @Component(modules = {TestUndertowModule.class, TestModule.class})
    @Singleton
    interface TestComponent {

        static Undertow createServer(int port) {
            TestComponent component = DaggerAvsApiTest_TestComponent.factory().create(port);
            return component.server();
        }

        Undertow server();

        @Component.Factory
        interface Factory {

            TestComponent create(@BindsInstance @Named("port") int port);
        }
    }
}
