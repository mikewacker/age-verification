package org.example.age.data.crypto;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class HmacUtilsTest {

    private static final byte[] MESSAGE = "Hello, world!".getBytes(StandardCharsets.UTF_8);

    private static byte[] key;

    @BeforeAll
    public static void generateKeys() {
        key = SecureRandomUtils.generateBytes(32);
    }

    @Test
    public void createHmac() {
        byte[] hmac = HmacUtils.createHmac(MESSAGE, key);
        assertThat(hmac).hasSize(32);
        assertThat(hmac).isNotEqualTo(new byte[32]);
    }
}
