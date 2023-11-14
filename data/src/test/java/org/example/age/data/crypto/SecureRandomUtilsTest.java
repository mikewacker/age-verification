package org.example.age.data.crypto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public final class SecureRandomUtilsTest {

    @Test
    public void generateBytes() {
        byte[] bytes = SecureRandomUtils.generateBytes(8);
        assertThat(bytes).hasSize(8);
        assertThat(bytes).isNotEqualTo(new byte[8]);
    }
}
