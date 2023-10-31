package org.example.age.common.site.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.example.age.data.SecureId;
import org.junit.jupiter.api.Test;

public final class AvsLocationTest {

    @Test
    public void urls() {
        AvsLocation location =
                AvsLocation.builder("localhost", 80).redirectPath("verify").build();
        assertThat(location.verificationSessionUrl("Site").toString())
                .isEqualTo("http://localhost/api/verification-session?site-id=Site");
        SecureId requestId = SecureId.fromString("4WLcuu0aZ2SMxC9FYVQegKv70i416C_k9fWkYjYhQlA");
        assertThat(location.redirectUrl(requestId).toString())
                .isEqualTo("http://localhost/verify?request-id=4WLcuu0aZ2SMxC9FYVQegKv70i416C_k9fWkYjYhQlA");
    }
}
