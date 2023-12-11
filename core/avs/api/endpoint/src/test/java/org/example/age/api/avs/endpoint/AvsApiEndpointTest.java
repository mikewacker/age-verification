package org.example.age.api.avs.endpoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import dagger.Component;
import io.undertow.server.HttpHandler;
import java.io.IOException;
import java.util.Map;
import javax.inject.Named;
import javax.inject.Singleton;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.common.VerificationState;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.SecureId;
import org.example.age.service.avs.endpoint.test.TestStubAvsServiceModule;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.TestServer;
import org.example.age.testing.server.undertow.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class AvsApiEndpointTest {

    @RegisterExtension
    private static final TestServer<?> avsServer =
            TestUndertowServer.register("avs", TestComponent::createApiHandler, "/api/");

    @Test
    public void verificationState() throws IOException {
        HttpOptional<VerificationState> maybeState = TestClient.apiRequestBuilder()
                .get(avsServer.url("/api/verification-state"))
                .headers(Map.of("Account-Id", "username", "User-Agent", "agent"))
                .executeWithJsonResponse(new TypeReference<>() {});
        assertThat(maybeState).isPresent();
    }

    @Test
    public void verificationSession() throws IOException {
        HttpOptional<VerificationSession> maybeSession = TestClient.apiRequestBuilder()
                .post(avsServer.url("/api/verification-session?site-id=Site"))
                .executeWithJsonResponse(new TypeReference<>() {});
        assertThat(maybeSession).isPresent();
    }

    @Test
    public void linkedVerificationRequest() throws IOException {
        SecureId requestId = SecureId.generate();
        int linkStatusCode = TestClient.apiRequestBuilder()
                .post(avsServer.url("/api/linked-verification-request?request-id=%s", requestId))
                .headers(Map.of("Account-Id", "username", "User-Agent", "agent"))
                .executeWithStatusCodeResponse();
        assertThat(linkStatusCode).isEqualTo(200);
    }

    @Test
    public void ageCertificate() throws IOException {
        int certificateStatusCode = TestClient.apiRequestBuilder()
                .post(avsServer.url("/api/age-certificate"))
                .headers(Map.of("Account-Id", "username", "User-Agent", "agent"))
                .executeWithStatusCodeResponse();
        assertThat(certificateStatusCode).isEqualTo(200);
    }

    @Test
    public void error_MissingAccountId() throws IOException {
        HttpOptional<VerificationState> maybeState = TestClient.apiRequestBuilder()
                .get(avsServer.url("/api/verification-state"))
                .headers(Map.of("User-Agent", "agent"))
                .executeWithJsonResponse(new TypeReference<>() {});
        assertThat(maybeState).isEmptyWithErrorCode(401);

        SecureId requestId = SecureId.generate();
        int linkStatusCode = TestClient.apiRequestBuilder()
                .post(avsServer.url("/api/linked-verification-request?request-id=%s", requestId))
                .headers(Map.of("User-Agent", "agent"))
                .executeWithStatusCodeResponse();
        assertThat(linkStatusCode).isEqualTo(401);

        int certificateStatusCode = TestClient.apiRequestBuilder()
                .post(avsServer.url("/api/age-certificate"))
                .headers(Map.of("User-Agent", "agent"))
                .executeWithStatusCodeResponse();
        assertThat(certificateStatusCode).isEqualTo(401);
    }

    @Test
    public void error_MissingSiteId() throws IOException {
        HttpOptional<VerificationSession> maybeSession = TestClient.apiRequestBuilder()
                .post(avsServer.url("/api/verification-session"))
                .executeWithJsonResponse(new TypeReference<>() {});
        assertThat(maybeSession).isEmptyWithErrorCode(400);
    }

    @Test
    public void error_MissingRequestId() throws IOException {
        int linkStatusCode = TestClient.apiRequestBuilder()
                .post(avsServer.url("/api/linked-verification-request"))
                .headers(Map.of("Account-Id", "username", "User-Agent", "agent"))
                .executeWithStatusCodeResponse();
        assertThat(linkStatusCode).isEqualTo(400);
    }

    @Test
    public void error_BadPath() throws IOException {
        int statusCode = TestClient.apiRequestBuilder()
                .get(avsServer.url("/api/does-not-exist"))
                .executeWithStatusCodeResponse();
        assertThat(statusCode).isEqualTo(404);
    }

    /** Dagger component that provides an {@link HttpHandler}. */
    @Component(modules = TestStubAvsServiceModule.class)
    @Singleton
    interface TestComponent {

        static HttpHandler createApiHandler() {
            TestComponent component = DaggerAvsApiEndpointTest_TestComponent.create();
            return component.apiHandler();
        }

        @Named("api")
        HttpHandler apiHandler();
    }
}
