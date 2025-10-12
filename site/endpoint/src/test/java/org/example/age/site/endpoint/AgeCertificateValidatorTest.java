package org.example.age.site.endpoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.client.WebStageTesting.await;
import static org.example.age.testing.client.WebStageTesting.awaitErrorCode;

import dagger.BindsInstance;
import dagger.Component;
import jakarta.inject.Singleton;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.function.Supplier;
import org.example.age.common.api.AgeCertificate;
import org.example.age.common.api.SignedAgeCertificate;
import org.example.age.common.api.VerificationRequest;
import org.example.age.common.api.crypto.SecureId;
import org.example.age.site.provider.certificateverifier.test.TestCertificateVerifierModule;
import org.example.age.testing.api.TestModels;
import org.example.age.testing.api.TestSignatures;
import org.junit.jupiter.api.Test;

public final class AgeCertificateValidatorTest {

    private final AgeCertificateValidator validator = TestComponent.create();

    @Test
    public void validate() {
        VerificationRequest request = TestModels.createVerificationRequest("site");
        AgeCertificate ageCertificate = TestModels.createAgeCertificate(request);
        SignedAgeCertificate signedAgeCertificate = TestSignatures.sign(ageCertificate);
        AgeCertificate validatedAgeCertificate = await(validator.validate(signedAgeCertificate));
        assertThat(validatedAgeCertificate).isEqualTo(ageCertificate);
    }

    @Test
    public void error_InvalidSignature() {
        VerificationRequest request = TestModels.createVerificationRequest("site");
        AgeCertificate ageCertificate = TestModels.createAgeCertificate(request);
        SignedAgeCertificate signedAgeCertificate = TestSignatures.signInvalid(ageCertificate, "secp256r1");
        awaitErrorCode(validator.validate(signedAgeCertificate), 401);
    }

    @Test
    public void error_WrongSite() {
        VerificationRequest request = TestModels.createVerificationRequest("other-site");
        AgeCertificate ageCertificate = TestModels.createAgeCertificate(request);
        SignedAgeCertificate signedAgeCertificate = TestSignatures.sign(ageCertificate);
        awaitErrorCode(validator.validate(signedAgeCertificate), 403);
    }

    @Test
    public void error_Expired() {
        OffsetDateTime expiration = OffsetDateTime.now(ZoneOffset.UTC).plus(Duration.ofMinutes(-5));
        VerificationRequest request = VerificationRequest.builder()
                .id(SecureId.generate())
                .siteId("site")
                .expiration(expiration)
                .build();
        AgeCertificate ageCertificate = TestModels.createAgeCertificate(request);
        SignedAgeCertificate signedAgeCertificate = TestSignatures.sign(ageCertificate);
        awaitErrorCode(validator.validate(signedAgeCertificate), 404);
    }

    /** Dagger component for {@link AgeCertificateValidator}. */
    @Component(modules = TestCertificateVerifierModule.class)
    @Singleton
    interface TestComponent extends Supplier<AgeCertificateValidator> {

        static AgeCertificateValidator create() {
            SiteEndpointConfig config = SiteEndpointConfig.builder()
                    .id("site")
                    .verifiedAccountExpiresIn(Duration.ofDays(30))
                    .build();
            return DaggerAgeCertificateValidatorTest_TestComponent.factory()
                    .create(config)
                    .get();
        }

        @Component.Factory
        interface Factory {

            TestComponent create(@BindsInstance SiteEndpointConfig config);
        }
    }
}
