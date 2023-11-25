package org.example.age.common.service.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import org.example.age.data.crypto.SecureId;
import org.example.age.data.utils.DataMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class AvsLocationTest {

    private AvsLocation location;

    @BeforeEach
    public void createAvsLocation() {
        location = AvsLocation.builder("localhost", 80).redirectPath("verify").build();
    }

    @Test
    public void urls() {
        assertThat(location.verificationSessionUrl("Site").toString())
                .isEqualTo("http://localhost/api/verification-session?site-id=Site");
        SecureId requestId = SecureId.fromString("4WLcuu0aZ2SMxC9FYVQegKv70i416C_k9fWkYjYhQlA");
        assertThat(location.redirectUrl(requestId).toString())
                .isEqualTo("http://localhost/verify?request-id=4WLcuu0aZ2SMxC9FYVQegKv70i416C_k9fWkYjYhQlA");
    }

    @Test
    public void serializeThenDeserialize() throws IOException {
        byte[] rawLocation = DataMapper.get().writeValueAsBytes(location);
        AvsLocation rtLocation = DataMapper.get().readValue(rawLocation, new TypeReference<>() {});
        assertThat(rtLocation).isEqualTo(location);
    }
}
