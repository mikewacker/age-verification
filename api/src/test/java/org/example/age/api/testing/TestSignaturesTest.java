package org.example.age.api.testing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.security.KeyPair;
import org.example.age.api.AgeCertificate;
import org.example.age.api.SignedAgeCertificate;
import org.junit.jupiter.api.Test;

public final class TestSignaturesTest {

    @Test
    public void getKeyPair() {
        KeyPair keyPair = TestSignatures.getKeyPair();
        assertThat(keyPair).isNotNull();
        assertThat(keyPair).isSameAs(TestSignatures.getKeyPair());
    }

    @Test
    public void signThenVerify() {
        AgeCertificate ageCertificate = TestModels.createAgeCertificate();
        AgeCertificate rtAgeCertificate = TestSignatures.verify(TestSignatures.sign(ageCertificate));
        assertThat(rtAgeCertificate).isEqualTo(ageCertificate);
    }

    @Test
    public void signInvalidThenVerify() {
        SignedAgeCertificate signedAgeCertificate =
                TestSignatures.signInvalid(TestModels.createAgeCertificate(), "secp256r1");
        assertThatThrownBy(() -> TestSignatures.verify(signedAgeCertificate));
    }
}
