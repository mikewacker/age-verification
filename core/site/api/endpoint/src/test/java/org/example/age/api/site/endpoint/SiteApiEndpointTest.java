package org.example.age.api.site.endpoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.common.VerificationState;
import org.example.age.data.certificate.AgeCertificate;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.AesGcmEncryptionPackage;
import org.example.age.data.crypto.DigitalSignature;
import org.example.age.data.crypto.SecureId;
import org.example.age.data.user.VerifiedUser;
import org.example.age.service.site.endpoint.test.StubSiteComponent;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.TestServer;
import org.example.age.testing.server.undertow.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class SiteApiEndpointTest {

    @RegisterExtension
    private static final TestServer<?> siteServer =
            TestUndertowServer.register("site", StubSiteComponent::createApiHandler, "/api/");

    @Test
    public void verificationState() throws IOException {
        HttpOptional<VerificationState> maybeState = TestClient.requestBuilder(
                        new TypeReference<VerificationState>() {})
                .get(siteServer.url("/api/verification-state"))
                .headers(Map.of("Account-Id", "username", "User-Agent", "agent"))
                .execute();
        assertThat(maybeState).isPresent();
    }

    @Test
    public void verificationSession() throws IOException {
        HttpOptional<VerificationSession> maybeSession = TestClient.requestBuilder(
                        new TypeReference<VerificationSession>() {})
                .post(siteServer.url("/api/verification-session"))
                .headers(Map.of("Account-Id", "username", "User-Agent", "agent"))
                .execute();
        assertThat(maybeSession).isPresent();
    }

    @Test
    public void ageCertificate() throws IOException {
        SignedAgeCertificate signedCertificate = createSignedAgeCertificate();
        int certificateStatusCode = TestClient.requestBuilder()
                .post(siteServer.url("/api/age-certificate"))
                .body(signedCertificate)
                .execute();
        assertThat(certificateStatusCode).isEqualTo(200);
    }

    @Test
    public void error_BadPath() throws IOException {
        int statusCode = TestClient.requestBuilder()
                .get(siteServer.url("/api/does-not-exist"))
                .execute();
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
}
