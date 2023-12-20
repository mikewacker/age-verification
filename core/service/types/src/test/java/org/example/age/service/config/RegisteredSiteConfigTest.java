package org.example.age.service.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.age.data.json.JsonValues;
import org.example.age.data.user.AgeThresholds;
import org.junit.jupiter.api.Test;

public final class RegisteredSiteConfigTest {

    @Test
    public void serializeThenDeserialize() {
        RegisteredSiteConfig registeredSiteConfig = RegisteredSiteConfig.builder("Site")
                .ageThresholds(AgeThresholds.of(13, 18))
                .build();
        byte[] rawRegisteredSiteConfig = JsonValues.serialize(registeredSiteConfig);
        RegisteredSiteConfig rtRegisteredSiteConfig =
                JsonValues.deserialize(rawRegisteredSiteConfig, new TypeReference<>() {});
        assertThat(rtRegisteredSiteConfig).isEqualTo(registeredSiteConfig);
    }
}
