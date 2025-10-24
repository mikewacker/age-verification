package org.example.age.common.provider.signingkey.demo;

import org.example.age.testing.config.TestConfigParser;
import org.junit.jupiter.api.Test;

public final class EcPublicKeyConfigTest {

    @Test
    public void parse() throws Exception {
        TestConfigParser.forClass(EcPublicKeyConfig.class)
                .parseLines(
                        "wX: 61340499596180719707288738669477306360190613239883629564918816825111167687915",
                        "wY: 38000387743223524528339467703153930999010297887656121516318277573781881204945");
    }
}
