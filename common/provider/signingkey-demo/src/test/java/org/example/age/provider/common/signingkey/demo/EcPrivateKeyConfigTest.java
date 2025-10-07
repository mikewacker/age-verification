package org.example.age.provider.common.signingkey.demo;

import java.io.IOException;
import java.math.BigInteger;
import org.example.age.testing.json.JsonTesting;
import org.junit.jupiter.api.Test;

public final class EcPrivateKeyConfigTest {

    @Test
    public void serializeThenDeserialize() throws IOException {
        EcPrivateKeyConfig config = EcPrivateKeyConfig.builder()
                .s(new BigInteger("87808632867103956881705523559918117434194472117688001288631494927155518459976"))
                .build();
        JsonTesting.serializeThenDeserialize(config, EcPrivateKeyConfig.class);
    }
}
