package org.example.age.api.endpoint.site;

import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.def.common.VerificationState;
import org.example.age.data.certificate.AgeCertificate;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.data.crypto.AesGcmEncryptionPackage;
import org.example.age.data.crypto.DigitalSignature;
import org.example.age.data.crypto.SecureId;
import org.example.age.data.user.VerifiedUser;
import org.example.age.service.component.stub.site.StubSiteComponent;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.TestServer;
import org.example.age.testing.server.undertow.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class SiteApiEndpointTest {

    @RegisterExtension
    private static final TestServer<?> siteServer =
            TestUndertowServer.register("site", "/api/", StubSiteComponent::createApiHandler);

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
    public void verificationRequest() throws IOException {
        HttpOptional<VerificationRequest> maybeRequest = TestClient.requestBuilder(
                        new TypeReference<VerificationRequest>() {})
                .post(siteServer.url("/api/verification-request"))
                .headers(Map.of("Account-Id", "username", "User-Agent", "agent"))
                .execute();
        assertThat(maybeRequest).isPresent();
    }

    @Test
    public void ageCertificate() throws IOException {
        SignedAgeCertificate signedCertificate = createStubSignedAgeCertificate();
        HttpOptional<String> maybeRedirectPath = TestClient.requestBuilder(new TypeReference<String>() {})
                .post(siteServer.url("/api/age-certificate"))
                .body(signedCertificate)
                .execute();
        assertThat(maybeRedirectPath).isPresent();
    }

    private static SignedAgeCertificate createStubSignedAgeCertificate() {
        VerificationRequest request = VerificationRequest.generateForSite("Site", Duration.ofMinutes(5), "");
        VerifiedUser user = VerifiedUser.of(SecureId.generate(), 18);
        AesGcmEncryptionPackage authToken = AesGcmEncryptionPackage.empty();
        AgeCertificate certificate = AgeCertificate.of(request, user, authToken);
        DigitalSignature signature = DigitalSignature.ofBytes(new byte[32]);
        return SignedAgeCertificate.of(certificate, signature);
    }
}
