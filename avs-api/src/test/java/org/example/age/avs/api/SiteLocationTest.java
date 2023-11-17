package org.example.age.avs.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public final class SiteLocationTest {

    @Test
    public void urls() {
        SiteLocation location =
                SiteLocation.builder("localhost", 80).redirectPath("verify").build();
        assertThat(location.ageCertificateUrl().toString()).isEqualTo("http://localhost/api/age-certificate");
        assertThat(location.redirectUrl().toString()).isEqualTo("http://localhost/verify");
    }
}
