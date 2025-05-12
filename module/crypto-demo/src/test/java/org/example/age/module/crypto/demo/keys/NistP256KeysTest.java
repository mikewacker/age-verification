package org.example.age.module.crypto.demo.keys;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import org.example.age.module.crypto.demo.testing.ConfigKeyPair;
import org.junit.jupiter.api.Test;

public final class NistP256KeysTest {

    @Test
    public void signThenVerify() throws Exception {
        byte[] message = "Hello, world!".getBytes(StandardCharsets.UTF_8);

        PrivateKey privateKey = NistP256Keys.toPrivateKey(ConfigKeyPair.privateKey());
        Signature signer = createSigner(privateKey);
        signer.update(message);
        byte[] signature = signer.sign();

        PublicKey publicKey = NistP256Keys.toPublicKey(ConfigKeyPair.publicKey());
        Signature verifier = createVerifier(publicKey);
        verifier.update(message);
        boolean verified = verifier.verify(signature);
        assertThat(verified).isTrue();
    }

    @Test
    public void nistP256Spec() throws Exception {
        ECParameterSpec nistP256RefSpec = createNistP256ReferenceSpec();
        assertThat(NistP256Keys.nistP256Spec.getCurve()).isEqualTo(nistP256RefSpec.getCurve());
        assertThat(NistP256Keys.nistP256Spec.getGenerator()).isEqualTo(nistP256RefSpec.getGenerator());
        assertThat(NistP256Keys.nistP256Spec.getOrder()).isEqualTo(nistP256RefSpec.getOrder());
        assertThat(NistP256Keys.nistP256Spec.getCofactor()).isEqualTo(nistP256RefSpec.getCofactor());
    }

    private static Signature createSigner(PrivateKey privateKey) throws Exception {
        Signature signer = Signature.getInstance("SHA256withECDSA");
        signer.initSign(privateKey);
        return signer;
    }

    private static Signature createVerifier(PublicKey publicKey) throws Exception {
        Signature verifier = Signature.getInstance("SHA256withECDSA");
        verifier.initVerify(publicKey);
        return verifier;
    }

    private static ECParameterSpec createNistP256ReferenceSpec() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
        keyPairGenerator.initialize(new ECGenParameterSpec("secp256r1"));
        ECPublicKey publicKey = (ECPublicKey) keyPairGenerator.generateKeyPair().getPublic();
        return publicKey.getParams();
    }
}
