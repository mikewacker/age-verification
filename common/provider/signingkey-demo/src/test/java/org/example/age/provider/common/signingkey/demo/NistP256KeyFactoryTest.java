package org.example.age.provider.common.signingkey.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.ECPoint;
import org.junit.jupiter.api.Test;

public final class NistP256KeyFactoryTest {

    @Test
    public void signThenVerify() throws Exception {
        byte[] message = "Hello, world!".getBytes(StandardCharsets.UTF_8);

        BigInteger s = new BigInteger("87808632867103956881705523559918117434194472117688001288631494927155518459976");
        PrivateKey privateKey = NistP256KeyFactory.createPrivate(s);
        Signature signer = Signature.getInstance("SHA256withECDSA");
        signer.initSign(privateKey);
        signer.update(message);
        byte[] signature = signer.sign();

        ECPoint w = new ECPoint(
                new BigInteger("61340499596180719707288738669477306360190613239883629564918816825111167687915"),
                new BigInteger("38000387743223524528339467703153930999010297887656121516318277573781881204945"));
        PublicKey publicKey = NistP256KeyFactory.createPublic(w);
        Signature verifier = Signature.getInstance("SHA256withECDSA");
        verifier.initVerify(publicKey);
        verifier.update(message);
        boolean verified = verifier.verify(signature);
        assertThat(verified).isTrue();
    }
}
