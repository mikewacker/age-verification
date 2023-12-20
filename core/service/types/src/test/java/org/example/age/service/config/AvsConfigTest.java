package org.example.age.service.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import java.time.Duration;
import org.example.age.data.json.JsonValues;
import org.junit.jupiter.api.Test;

public final class AvsConfigTest {

    @Test
    public void serializeThenDeserialize() {
        AvsConfig avsConfig = AvsConfig.builder()
                .verificationSessionExpiresIn(Duration.ofMinutes(5).toSeconds())
                .redirectPath("/verify")
                .build();
        byte[] rawAvsConfig = JsonValues.serialize(avsConfig);
        AvsConfig rtAvsConfig = JsonValues.deserialize(rawAvsConfig, new TypeReference<>() {});
        assertThat(rtAvsConfig).isEqualTo(avsConfig);
    }
}
