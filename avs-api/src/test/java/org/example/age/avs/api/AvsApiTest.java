package org.example.age.avs.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import dagger.Component;
import io.undertow.server.HttpHandler;
import java.io.IOException;
import java.util.Map;
import javax.inject.Named;
import javax.inject.Singleton;
import okhttp3.Response;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.SecureId;
import org.example.age.test.avs.service.StubAvsServiceModule;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class AvsApiTest {

    @RegisterExtension
    private static final TestUndertowServer avsServer =
            TestUndertowServer.fromHandlerAtPath(TestComponent::createHandler, "/api/");

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

    /** Dagger component that provides an {@link HttpHandler}. */
    @Component(modules = StubAvsServiceModule.class)
    @Singleton
    interface TestComponent {

        static HttpHandler createHandler() {
            TestComponent component = DaggerAvsApiTest_TestComponent.create();
            return component.handler();
        }

        @Named("api")
        HttpHandler handler();
    }
}
