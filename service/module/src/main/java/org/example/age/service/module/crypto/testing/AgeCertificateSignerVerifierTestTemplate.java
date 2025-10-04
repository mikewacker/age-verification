package org.example.age.service.module.crypto.testing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.client.WebStageTesting.await;
import static org.example.age.testing.client.WebStageTesting.awaitErrorCode;

import org.example.age.avs.spi.AgeCertificateSigner;
import org.example.age.common.api.AgeCertificate;
import org.example.age.common.api.SignedAgeCertificate;
import org.example.age.site.spi.AgeCertificateVerifier;
import org.example.age.testing.api.TestModels;
import org.example.age.testing.api.TestSignatures;
import org.junit.jupiter.api.Test;

public abstract class AgeCertificateSignerVerifierTestTemplate {

    @Test
    public void signThenVerify() {
        AgeCertificate ageCertificate = TestModels.createAgeCertificate();
        AgeCertificate rtAgeCertificate = await(signer().sign(ageCertificate).thenCompose(verifier()::verify));
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

    protected abstract AgeCertificateSigner signer();

    protected abstract AgeCertificateVerifier verifier();
}
