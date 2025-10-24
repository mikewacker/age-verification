package org.example.age.common.provider.signingkey.demo;

import org.example.age.testing.config.TestConfigParser;
import org.junit.jupiter.api.Test;

public final class EcPrivateKeyConfigTest {

    @Test
    public void parse() throws Exception {
        TestConfigParser.forClass(EcPrivateKeyConfig.class)
                .parseLines("s: 87808632867103956881705523559918117434194472117688001288631494927155518459976");
    }
}
