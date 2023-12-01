package org.example.age.site.api;

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
import org.example.age.data.certificate.AgeCertificate;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.AesGcmEncryptionPackage;
import org.example.age.data.crypto.BytesValue;
import org.example.age.data.crypto.DigitalSignature;
import org.example.age.data.crypto.SecureId;
import org.example.age.data.user.VerifiedUser;
import org.example.age.test.site.service.StubSiteServiceModule;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class SiteApiTest {

    @RegisterExtension
    private static final TestUndertowServer siteServer =
            TestUndertowServer.fromHandlerAtPath(TestComponent::createHandler, "/api/");

    @Test
    public void verify() throws IOException {
        HttpOptional<VerificationSession> maybeSession = TestClient.apiRequestBuilder()
                .url(siteServer.url("/api/verification-session"))
                .headers(Map.of("Account-Id", "username", "User-Agent", "agent"))
                .post()
                .executeWithJsonResponse(new TypeReference<>() {});
        assertThat(maybeSession).isPresent();
        VerificationSession session = maybeSession.get();

        SignedAgeCertificate signedCertificate = createSignedAgeCertificate(session);
        int certificateStatusCode = TestClient.apiRequestBuilder()
                .url(siteServer.url("/api/age-certificate"))
                .post(signedCertificate)
                .executeWithStatusCodeResponse();
        assertThat(certificateStatusCode).isEqualTo(200);
    }

    @Test
    public void error_MissingAccountId() throws IOException {
        HttpOptional<VerificationSession> maybeSession = TestClient.apiRequestBuilder()
                .url(siteServer.url("/api/verification-session"))
                .headers(Map.of("User-Agent", "agent"))
                .post()
                .executeWithJsonResponse(new TypeReference<>() {});
        assertThat(maybeSession).isEmptyWithErrorCode(401);
    }

    @Test
    public void error_MissingSignedAgeCertificate() throws IOException {
        int certificateStatusCode = TestClient.apiRequestBuilder()
                .url(siteServer.url("/api/age-certificate"))
                .post()
                .executeWithStatusCodeResponse();
        assertThat(certificateStatusCode).isEqualTo(400);
    }

    @Test
    public void error_BadPath() throws IOException {
        int statusCode = TestClient.apiRequestBuilder()
                .url(siteServer.url("/api/does-not-exist"))
                .get()
                .executeWithStatusCodeResponse();
        assertThat(statusCode).isEqualTo(404);
    }

    private static SignedAgeCertificate createSignedAgeCertificate(VerificationSession session) {
        VerifiedUser user = VerifiedUser.of(SecureId.generate(), 18);
        AesGcmEncryptionPackage authToken = AesGcmEncryptionPackage.of(BytesValue.empty(), BytesValue.empty());
        AgeCertificate certificate = AgeCertificate.of(session.verificationRequest(), user, authToken);
        DigitalSignature signature = DigitalSignature.ofBytes(new byte[32]);
        return SignedAgeCertificate.of(certificate, signature);
    }

    /** Dagger component that provides an {@link HttpHandler}. */
    @Component(modules = StubSiteServiceModule.class)
    @Singleton
    interface TestComponent {

        static HttpHandler createHandler() {
            TestComponent component = DaggerSiteApiTest_TestComponent.create();
            return component.handler();
        }

        @Named("api")
        HttpHandler handler();
    }
}
