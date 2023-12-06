package org.example.age.avs.service.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.age.api.JsonObjects;
import org.example.age.common.service.config.SiteLocation;
import org.example.age.data.user.AgeThresholds;
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
        byte[] rawRegisteredSiteConfig = JsonObjects.serialize(registeredSiteConfig);
        RegisteredSiteConfig rtRegisteredSiteConfig =
                JsonObjects.deserialize(rawRegisteredSiteConfig, new TypeReference<>() {});
        assertThat(rtRegisteredSiteConfig).isEqualTo(registeredSiteConfig);
    }
}
