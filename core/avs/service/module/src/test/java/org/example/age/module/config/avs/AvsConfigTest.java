package org.example.age.module.config.avs;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import java.time.Duration;
import org.example.age.api.JsonObjects;
import org.junit.jupiter.api.Test;

public final class AvsConfigTest {

    @Test
    public void serializeThenDeserialize() {
        AvsConfig avsConfig = AvsConfig.builder()
                .verificationSessionExpiresIn(Duration.ofMinutes(5).toSeconds())
                .build();
        byte[] rawAvsConfig = JsonObjects.serialize(avsConfig);
        AvsConfig rtAvsConfig = JsonObjects.deserialize(rawAvsConfig, new TypeReference<>() {});
        assertThat(rtAvsConfig).isEqualTo(avsConfig);
    }
}
