package org.example.age.testing.site.spi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.client.WebStageTesting.await;
import static org.example.age.testing.client.WebStageTesting.awaitErrorCode;

import java.security.PrivateKey;
import org.example.age.common.api.AgeCertificate;
import org.example.age.common.api.SignedAgeCertificate;
import org.example.age.site.spi.AgeCertificateVerifier;
import org.example.age.testing.api.TestModels;
import org.example.age.testing.api.TestSignatures;
import org.junit.jupiter.api.Test;

public abstract class CertificateVerifierTestTemplate {

    @Test
    public void verify() {
        AgeCertificate ageCertificate = TestModels.createAgeCertificate();
        SignedAgeCertificate signedAgeCertificate = TestSignatures.sign(ageCertificate, privateKey());
        AgeCertificate rtAgeCertificate = await(verifier().verify(signedAgeCertificate));
        assertThat(rtAgeCertificate).isEqualTo(ageCertificate);
    }

    @Test
    public void error_AlgorithmNotImplemented() {
        AgeCertificate ageCertificate = TestModels.createAgeCertificate();
        SignedAgeCertificate signedAgeCertificate = TestSignatures.signInvalid(ageCertificate, "dne");
        awaitErrorCode(verifier().verify(signedAgeCertificate), 501);
    }

    @Test
    public void error_InvalidSignature() {
        AgeCertificate ageCertificate = TestModels.createAgeCertificate();
        SignedAgeCertificate signedAgeCertificate = TestSignatures.signInvalid(ageCertificate, "secp256r1");
        awaitErrorCode(verifier().verify(signedAgeCertificate), 401);
    }

    protected abstract AgeCertificateVerifier verifier();

    protected abstract PrivateKey privateKey();
}
