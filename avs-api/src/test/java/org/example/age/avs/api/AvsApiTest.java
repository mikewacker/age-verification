package org.example.age.avs.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import dagger.Component;
import io.undertow.server.HttpHandler;
import java.io.IOException;
import java.util.Map;
import javax.inject.Named;
import javax.inject.Singleton;
import org.example.age.api.HttpOptional;
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
        HttpOptional<VerificationSession> maybeSession = TestClient.apiRequestBuilder()
                .url(avsServer.url("/api/verification-session?site-id=Site"))
                .post()
                .executeWithJsonResponse(new TypeReference<>() {});
        assertThat(maybeSession).isPresent();
        VerificationSession session = maybeSession.get();
        assertThat(session.verificationRequest().siteId()).isEqualTo("Site");

        SecureId requestId = session.verificationRequest().id();
        int linkStatusCode = TestClient.apiRequestBuilder()
                .url(avsServer.url("/api/linked-verification-request?request-id=%s", requestId))
                .headers(Map.of("Account-Id", "username", "User-Agent", "agent"))
                .post()
                .executeWithStatusCodeResponse();
        assertThat(linkStatusCode).isEqualTo(200);

        int certificateStatusCode = TestClient.apiRequestBuilder()
                .url(avsServer.url("/api/age-certificate"))
                .headers(Map.of("Account-Id", "username", "User-Agent", "agent"))
                .post()
                .executeWithStatusCodeResponse();
        assertThat(certificateStatusCode).isEqualTo(200);
    }

    @Test
    public void error_MissingAccountId() throws IOException {
        SecureId requestId = SecureId.generate();
        int linkStatusCode = TestClient.apiRequestBuilder()
                .url(avsServer.url("/api/linked-verification-request?request-id=%s", requestId))
                .headers(Map.of("User-Agent", "agent"))
                .post()
                .executeWithStatusCodeResponse();
        assertThat(linkStatusCode).isEqualTo(401);

        int certificateStatusCode = TestClient.apiRequestBuilder()
                .url(avsServer.url("/api/age-certificate"))
                .headers(Map.of("User-Agent", "agent"))
                .post()
                .executeWithStatusCodeResponse();
        assertThat(certificateStatusCode).isEqualTo(401);
    }

    @Test
    public void error_MissingSiteId() throws IOException {
        HttpOptional<VerificationSession> maybeSession = TestClient.apiRequestBuilder()
                .url(avsServer.url("/api/verification-session"))
                .post()
                .executeWithJsonResponse(new TypeReference<>() {});
        assertThat(maybeSession).isEmptyWithErrorCode(400);
    }

    @Test
    public void error_MissingRequestId() throws IOException {
        int linkStatusCode = TestClient.apiRequestBuilder()
                .url(avsServer.url("/api/linked-verification-request"))
                .headers(Map.of("Account-Id", "username", "User-Agent", "agent"))
                .post()
                .executeWithStatusCodeResponse();
        assertThat(linkStatusCode).isEqualTo(400);
    }

    @Test
    public void error_BadPath() throws IOException {
        int statusCode = TestClient.apiRequestBuilder()
                .url(avsServer.url("/api/does-not-exist"))
                .get()
                .executeWithStatusCodeResponse();
        assertThat(statusCode).isEqualTo(404);
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
