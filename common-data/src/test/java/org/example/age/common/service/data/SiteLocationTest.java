package org.example.age.common.service.data;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import org.example.age.data.utils.DataMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class SiteLocationTest {

    private SiteLocation location;

    @BeforeEach
    public void createSiteLocation() {
        location = SiteLocation.builder("localhost", 80).redirectPath("verify").build();
    }

    @Test
    public void urls() {
        assertThat(location.ageCertificateUrl().toString()).isEqualTo("http://localhost/api/age-certificate");
        assertThat(location.redirectUrl().toString()).isEqualTo("http://localhost/verify");
    }

    @Test
    public void serializeThenDeserialize() throws IOException {
        byte[] rawLocation = DataMapper.get().writeValueAsBytes(location);
        SiteLocation rtLocation = DataMapper.get().readValue(rawLocation, new TypeReference<>() {});
        assertThat(rtLocation).isEqualTo(location);
    }
}
