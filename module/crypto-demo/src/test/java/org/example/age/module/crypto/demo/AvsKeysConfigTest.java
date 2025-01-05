package org.example.age.module.crypto.demo;

import java.io.IOException;
import org.example.age.api.crypto.SecureId;
import org.example.age.module.crypto.demo.testing.ConfigKeyPair;
import org.example.age.testing.JsonTesting;
import org.junit.jupiter.api.Test;

public final class AvsKeysConfigTest {

    @Test
    public void serializeThenDeserialize() throws IOException {
        AvsKeysConfig config = AvsKeysConfig.builder()
                .putLocalization("site", SecureId.generate())
                .signing(ConfigKeyPair.privateKey())
                .build();
        JsonTesting.serializeThenDeserialize(config, AvsKeysConfig.class);
    }
}
