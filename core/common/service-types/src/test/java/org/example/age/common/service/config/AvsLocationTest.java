package org.example.age.common.service.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.age.api.JsonSerializer;
import org.example.age.data.crypto.SecureId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class AvsLocationTest {

    private AvsLocation location;

    @BeforeEach
    public void createAvsLocation() {
        location = AvsLocation.builder("localhost", 80).redirectPath("/verify").build();
    }

    @Test
    public void urls() {
        assertThat(location.verificationSessionUrl("Site"))
                .isEqualTo("http://localhost:80/api/verification-session?site-id=Site");
        SecureId requestId = SecureId.fromString("4WLcuu0aZ2SMxC9FYVQegKv70i416C_k9fWkYjYhQlA");
        assertThat(location.redirectUrl(requestId))
                .isEqualTo("http://localhost:80/verify?request-id=4WLcuu0aZ2SMxC9FYVQegKv70i416C_k9fWkYjYhQlA");
    }

    @Test
    public void serializeThenDeserialize() {
        byte[] rawLocation = JsonSerializer.serialize(location);
        AvsLocation rtLocation = JsonSerializer.deserialize(rawLocation, new TypeReference<>() {});
        assertThat(rtLocation).isEqualTo(location);
    }
}
