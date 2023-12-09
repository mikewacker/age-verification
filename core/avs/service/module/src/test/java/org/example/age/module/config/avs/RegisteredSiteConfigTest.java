package org.example.age.module.config.avs;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.age.data.json.JsonValues;
import org.example.age.data.user.AgeThresholds;
import org.example.age.module.config.common.SiteLocation;
import org.junit.jupiter.api.Test;

public final class RegisteredSiteConfigTest {

    @Test
    public void serializeThenDeserialize() {
        SiteLocation location =
                SiteLocation.builder("localhost", 80).redirectPath("/verify").build();
        RegisteredSiteConfig registeredSiteConfig = RegisteredSiteConfig.builder("Site")
                .location(location)
                .ageThresholds(AgeThresholds.of(13, 18))
                .build();
        byte[] rawRegisteredSiteConfig = JsonValues.serialize(registeredSiteConfig);
        RegisteredSiteConfig rtRegisteredSiteConfig =
                JsonValues.deserialize(rawRegisteredSiteConfig, new TypeReference<>() {});
        assertThat(rtRegisteredSiteConfig).isEqualTo(registeredSiteConfig);
    }
}
