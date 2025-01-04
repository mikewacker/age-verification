package org.example.age.module.client;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import org.example.age.testing.TestObjectMapper;
import org.junit.jupiter.api.Test;

public final class AvsClientsConfigTest {

    @Test
    public void serializeThenDeserialize() throws Exception {
        AvsClientsConfig config = AvsClientsConfig.builder()
                .putSiteUrls("site", new URI("http://localhost:8080").toURL())
                .build();
        String json = TestObjectMapper.get().writeValueAsString(config);
        AvsClientsConfig rtConfig = TestObjectMapper.get().readValue(json, AvsClientsConfig.class);
        assertThat(rtConfig).isEqualTo(config);
    }
}
