package org.example.age.common.provider.signingkey.demo;

import java.io.IOException;
import java.math.BigInteger;
import org.example.age.testing.json.JsonTesting;
import org.junit.jupiter.api.Test;

public final class EcPublicKeyConfigTest {

    @Test
    public void serializeThenDeserialize() throws IOException {
        EcPublicKeyConfig config = EcPublicKeyConfig.builder()
                .wX(new BigInteger("61340499596180719707288738669477306360190613239883629564918816825111167687915"))
                .wY(new BigInteger("38000387743223524528339467703153930999010297887656121516318277573781881204945"))
                .build();
        JsonTesting.serializeThenDeserialize(config, EcPublicKeyConfig.class);
    }
}
