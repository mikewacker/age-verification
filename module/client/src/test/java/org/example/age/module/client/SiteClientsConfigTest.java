package org.example.age.module.client;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import org.example.age.testing.TestObjectMapper;
import org.junit.jupiter.api.Test;

public final class SiteClientsConfigTest {

    @Test
    public void serializeThenDeserialize() throws Exception {
        SiteClientsConfig config = SiteClientsConfig.builder()
                .avsUrl(new URI("http://localhost:8080").toURL())
                .build();
        String json = TestObjectMapper.get().writeValueAsString(config);
        SiteClientsConfig rtConfig = TestObjectMapper.get().readValue(json, SiteClientsConfig.class);
        assertThat(rtConfig).isEqualTo(config);
    }
}
