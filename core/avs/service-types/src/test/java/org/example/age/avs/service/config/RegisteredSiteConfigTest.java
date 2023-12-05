package org.example.age.avs.service.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.example.age.common.service.config.SiteLocation;
import org.example.age.data.user.AgeThresholds;
import org.junit.jupiter.api.Test;

public final class RegisteredSiteConfigTest {

    @Test
    public void serializeThenDeserialize() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        SiteLocation location =
                SiteLocation.builder("localhost", 80).redirectPath("/verify").build();
        RegisteredSiteConfig registeredSiteConfig = RegisteredSiteConfig.builder("Site")
                .location(location)
                .ageThresholds(AgeThresholds.of(13, 18))
                .build();
        byte[] rawRegisteredSiteConfig = mapper.writeValueAsBytes(registeredSiteConfig);
        RegisteredSiteConfig rtRegisteredSiteConfig =
                mapper.readValue(rawRegisteredSiteConfig, new TypeReference<>() {});
        assertThat(rtRegisteredSiteConfig).isEqualTo(registeredSiteConfig);
    }
}
