package org.example.age.module.crypto.demo;

import java.io.IOException;
import org.example.age.api.crypto.SecureId;
import org.example.age.module.crypto.demo.testing.ConfigKeyPair;
import org.example.age.testing.JsonTesting;
import org.junit.jupiter.api.Test;

public final class SiteKeysConfigTest {

    @Test
    public void serializeThenDeserialize() throws IOException {
        SiteKeysConfig config = SiteKeysConfig.builder()
                .localization(SecureId.generate())
                .signing(ConfigKeyPair.publicKey())
                .build();
        JsonTesting.serializeThenDeserialize(config, SiteKeysConfig.class);
    }
}
