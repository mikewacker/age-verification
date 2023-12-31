package org.example.age.service.config;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.mikewacker.drift.testing.json.JsonTester;
import org.example.age.data.user.AgeThresholds;
import org.junit.jupiter.api.Test;

public final class RegisteredSiteConfigTest {

    @Test
    public void serializeThenDeserialize() {
        RegisteredSiteConfig siteConfig = RegisteredSiteConfig.builder("Site")
                .ageThresholds(AgeThresholds.of(13, 18))
                .build();
        JsonTester.serializeThenDeserialize(siteConfig, new TypeReference<>() {});
    }
}
