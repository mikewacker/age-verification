package org.example.age.service.config;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.mikewacker.drift.testing.json.JsonTester;
import java.time.Duration;
import org.junit.jupiter.api.Test;

public final class AvsConfigTest {

    @Test
    public void serializeThenDeserialize() {
        AvsConfig avsConfig = AvsConfig.builder()
                .verificationSessionExpiresIn(Duration.ofMinutes(5).toSeconds())
                .redirectPath("/verify")
                .build();
        JsonTester.serializeThenDeserialize(avsConfig, new TypeReference<>() {});
    }
}
