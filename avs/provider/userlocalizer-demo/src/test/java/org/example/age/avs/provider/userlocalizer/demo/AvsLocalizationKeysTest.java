package org.example.age.avs.provider.userlocalizer.demo;

import java.io.IOException;
import org.example.age.common.api.crypto.SecureId;
import org.example.age.testing.json.JsonTesting;
import org.junit.jupiter.api.Test;

public final class AvsLocalizationKeysTest {

    @Test
    public void serializeThenDeserialize() throws IOException {
        AvsLocalizationKeysConfig config = AvsLocalizationKeysConfig.builder()
                .putKeys("site", SecureId.generate())
                .build();
        JsonTesting.serializeThenDeserialize(config, AvsLocalizationKeysConfig.class);
    }
}
