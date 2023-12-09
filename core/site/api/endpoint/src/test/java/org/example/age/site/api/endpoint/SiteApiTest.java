package org.example.age.site.api.endpoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import dagger.Component;
import io.undertow.server.HttpHandler;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import javax.inject.Named;
import javax.inject.Singleton;
import org.example.age.api.base.HttpOptional;
import org.example.age.common.api.data.VerificationState;
import org.example.age.data.certificate.AgeCertificate;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.AesGcmEncryptionPackage;
import org.example.age.data.crypto.DigitalSignature;
import org.example.age.data.crypto.SecureId;
import org.example.age.data.user.VerifiedUser;
import org.example.age.site.service.endpoint.test.TestSiteServiceModule;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.TestServer;
import org.example.age.testing.server.undertow.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class SiteApiTest {

    @RegisterExtension
    private static final TestServer<?> siteServer =
            TestUndertowServer.register("site", TestComponent::createApiHandler, "/api/");

    @Test
    public void verificationState() throws IOException {
        HttpOptional<VerificationState> maybeState = TestClient.apiRequestBuilder()
                .get(siteServer.url("/api/verification-state"))
                .headers(Map.of("Account-Id", "username", "User-Agent", "agent"))
                .executeWithJsonResponse(new TypeReference<>() {});
        assertThat(maybeState).isPresent();
    }

    @Test
    public void verificationSession() throws IOException {
        HttpOptional<VerificationSession> maybeSession = TestClient.apiRequestBuilder()
                .post(siteServer.url("/api/verification-session"))
                .headers(Map.of("Account-Id", "username", "User-Agent", "agent"))
                .executeWithJsonResponse(new TypeReference<>() {});
        assertThat(maybeSession).isPresent();
    }

    @Test
    public void ageCertificate() throws IOException {
        SignedAgeCertificate signedCertificate = createSignedAgeCertificate();
        int certificateStatusCode = TestClient.apiRequestBuilder()
                .post(siteServer.url("/api/age-certificate"))
                .body(signedCertificate)
                .executeWithStatusCodeResponse();
        assertThat(certificateStatusCode).isEqualTo(200);
    }

    @Test
    public void error_MissingAccountId() throws IOException {
        HttpOptional<VerificationState> maybeState = TestClient.apiRequestBuilder()
                .get(siteServer.url("/api/verification-state"))
                .headers(Map.of("User-Agent", "agent"))
                .executeWithJsonResponse(new TypeReference<>() {});
        assertThat(maybeState).isEmptyWithErrorCode(401);

        HttpOptional<VerificationSession> maybeSession = TestClient.apiRequestBuilder()
                .post(siteServer.url("/api/verification-session"))
                .headers(Map.of("User-Agent", "agent"))
                .executeWithJsonResponse(new TypeReference<>() {});
        assertThat(maybeSession).isEmptyWithErrorCode(401);
    }

    @Test
    public void error_MissingSignedAgeCertificate() throws IOException {
        int certificateStatusCode = TestClient.apiRequestBuilder()
                .post(siteServer.url("/api/age-certificate"))
                .executeWithStatusCodeResponse();
        assertThat(certificateStatusCode).isEqualTo(400);
    }

    @Test
    public void error_BadPath() throws IOException {
        int statusCode = TestClient.apiRequestBuilder()
                .get(siteServer.url("/api/does-not-exist"))
                .executeWithStatusCodeResponse();
        assertThat(statusCode).isEqualTo(404);
    }

    private static SignedAgeCertificate createSignedAgeCertificate() {
        VerificationRequest request = VerificationRequest.generateForSite("Site", Duration.ofMinutes(5));
        VerifiedUser user = VerifiedUser.of(SecureId.generate(), 18);
        AesGcmEncryptionPackage authToken = AesGcmEncryptionPackage.empty();
        AgeCertificate certificate = AgeCertificate.of(request, user, authToken);
        DigitalSignature signature = DigitalSignature.ofBytes(new byte[32]);
        return SignedAgeCertificate.of(certificate, signature);
    }

    /** Dagger component that provides an {@link HttpHandler}. */
    @Component(modules = TestSiteServiceModule.class)
    @Singleton
    interface TestComponent {

        static HttpHandler createApiHandler() {
            TestComponent component = DaggerSiteApiTest_TestComponent.create();
            return component.apiHandler();
        }

        @Named("api")
        HttpHandler apiHandler();
    }
}
