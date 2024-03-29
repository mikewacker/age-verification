package org.example.age.api.endpoint;

import static io.github.mikewacker.drift.testing.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.mikewacker.drift.api.HttpOptional;
import io.github.mikewacker.drift.client.JsonApiClient;
import io.github.mikewacker.drift.testing.server.TestServer;
import io.github.mikewacker.drift.testing.server.TestUndertowServer;
import java.io.IOException;
import java.time.Duration;
import org.example.age.api.def.VerificationState;
import org.example.age.data.certificate.AgeCertificate;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.data.crypto.AesGcmEncryptionPackage;
import org.example.age.data.crypto.DigitalSignature;
import org.example.age.data.crypto.SecureId;
import org.example.age.data.user.VerifiedUser;
import org.example.age.service.component.stub.StubSiteComponent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class SiteApiEndpointTest {

    @RegisterExtension
    private static final TestServer<?> siteServer =
            TestUndertowServer.register("site", "/api/", StubSiteComponent::createApiHandler);

    @Test
    public void getVerificationState() throws IOException {
        HttpOptional<VerificationState> maybeState = JsonApiClient.requestBuilder()
                .jsonResponse(new TypeReference<VerificationState>() {})
                .get(siteServer.url("/api/verification-state"))
                .header("Account-Id", "username")
                .header("User-Agent", "agent")
                .build()
                .execute();
        assertThat(maybeState).isPresent();
    }

    @Test
    public void createVerificationRequest() throws IOException {
        HttpOptional<VerificationRequest> maybeRequest = JsonApiClient.requestBuilder()
                .jsonResponse(new TypeReference<VerificationRequest>() {})
                .post(siteServer.url("/api/verification-request/create"))
                .header("Account-Id", "username")
                .header("User-Agent", "agent")
                .build()
                .execute();
        assertThat(maybeRequest).isPresent();
    }

    @Test
    public void processAgeCertificate() throws IOException {
        SignedAgeCertificate signedCertificate = createStubSignedAgeCertificate();
        HttpOptional<String> maybeRedirectPath = JsonApiClient.requestBuilder()
                .jsonResponse(new TypeReference<String>() {})
                .post(siteServer.url("/api/age-certificate/process"))
                .body(signedCertificate)
                .build()
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
