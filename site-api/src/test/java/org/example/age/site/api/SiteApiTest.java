package org.example.age.site.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import dagger.Component;
import io.undertow.server.HttpHandler;
import java.io.IOException;
import java.util.Map;
import javax.inject.Named;
import javax.inject.Singleton;
import okhttp3.Response;
import org.example.age.data.certificate.AgeCertificate;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationRequest;
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
        String sessionUrl = siteServer.url("/api/verification-session");
        Map<String, String> userHeaders = Map.of("Account-Id", "username", "User-Agent", "agent");
        Response sessionResponse = TestClient.post(sessionUrl, userHeaders);
        assertThat(sessionResponse.code()).isEqualTo(200);
        VerificationSession session = TestClient.readBody(sessionResponse, new TypeReference<>() {});
        assertThat(session.verificationRequest().siteId()).isEqualTo("Site");

        String certificateUrl = siteServer.url("/api/age-certificate");
        SignedAgeCertificate signedCertificate = createSignedAgeCertificate(session);
        Response certificateResponse = TestClient.post(certificateUrl, signedCertificate);
        assertThat(certificateResponse.code()).isEqualTo(200);
    }

    @Test
    public void error_MissingAccountId() throws IOException {
        String sessionUrl = siteServer.url("/api/verification-session");
        Map<String, String> userHeaders = Map.of("User-Agent", "agent");
        Response sessionResponse = TestClient.post(sessionUrl, userHeaders);
        assertThat(sessionResponse.code()).isEqualTo(401);
    }

    @Test
    public void error_MissingSignedAgeCertificate() throws IOException {
        String certificateUrl = siteServer.url("/api/age-certificate");
        Response certificateResponse = TestClient.post(certificateUrl);
        assertThat(certificateResponse.code()).isEqualTo(400);
    }

    @Test
    public void error_BadPath() throws IOException {
        String url = siteServer.url("/api/does-not-exist");
        Response response = TestClient.post(url);
        assertThat(response.code()).isEqualTo(404);
    }

    private static SignedAgeCertificate createSignedAgeCertificate(VerificationSession session) {
        VerificationRequest request = session.verificationRequest();
        VerifiedUser user = VerifiedUser.of(SecureId.generate(), 18);
        AesGcmEncryptionPackage authToken = AesGcmEncryptionPackage.of(BytesValue.empty(), BytesValue.empty());
        AgeCertificate certificate = AgeCertificate.of(request, user, authToken);
        DigitalSignature signature = DigitalSignature.ofBytes(new byte[1024]);
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
