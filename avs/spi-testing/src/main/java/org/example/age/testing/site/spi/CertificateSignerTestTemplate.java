package org.example.age.testing.site.spi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.client.WebStageTesting.await;

import java.security.PublicKey;
import org.example.age.avs.spi.AgeCertificateSigner;
import org.example.age.common.api.AgeCertificate;
import org.example.age.common.api.SignedAgeCertificate;
import org.example.age.testing.api.TestModels;
import org.example.age.testing.api.TestSignatures;
import org.junit.jupiter.api.Test;

public abstract class CertificateSignerTestTemplate {

    @Test
    public void sign() {
        AgeCertificate ageCertificate = TestModels.createAgeCertificate();
        SignedAgeCertificate signedAgeCertificate = await(signer().sign(ageCertificate));
        AgeCertificate rtAgeCertificate = TestSignatures.verify(signedAgeCertificate, publicKey());
        assertThat(rtAgeCertificate).isEqualTo(ageCertificate);
    }

    protected abstract AgeCertificateSigner signer();

    protected abstract PublicKey publicKey();
}
