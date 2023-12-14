package org.example.age.module.location.common;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.age.data.crypto.SecureId;
import org.example.age.data.json.JsonValues;
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
        SecureId requestId = SecureId.generate();
        String expectedRedirectUrl = String.format("http://localhost:80/verify?request-id=%s", requestId);
        assertThat(location.redirectUrl(requestId)).isEqualTo(expectedRedirectUrl);
    }

    @Test
    public void serializeThenDeserialize() {
        byte[] rawLocation = JsonValues.serialize(location);
        AvsLocation rtLocation = JsonValues.deserialize(rawLocation, new TypeReference<>() {});
        assertThat(rtLocation).isEqualTo(location);
    }
}
