package org.example.age.common.service.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class SiteLocationTest {

    private SiteLocation location;

    @BeforeEach
    public void createSiteLocation() {
        location = SiteLocation.builder("localhost", 80).redirectPath("/verify").build();
    }

    @Test
    public void urls() {
        assertThat(location.ageCertificateUrl()).isEqualTo("http://localhost:80/api/age-certificate");
        assertThat(location.redirectUrl()).isEqualTo("http://localhost:80/verify");
    }

    @Test
    public void serializeThenDeserialize() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        byte[] rawLocation = mapper.writeValueAsBytes(location);
        SiteLocation rtLocation = mapper.readValue(rawLocation, new TypeReference<>() {});
        assertThat(rtLocation).isEqualTo(location);
    }
}
