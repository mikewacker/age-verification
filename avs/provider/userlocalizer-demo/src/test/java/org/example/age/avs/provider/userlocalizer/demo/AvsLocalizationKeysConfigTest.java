package org.example.age.avs.provider.userlocalizer.demo;

import org.example.age.testing.config.TestConfigParser;
import org.junit.jupiter.api.Test;

public final class AvsLocalizationKeysConfigTest {

    @Test
    public void parse() throws Exception {
        TestConfigParser.forClass(AvsLocalizationKeysConfig.class)
                .parseLines("keys:", "  site: pER-dDPdsvdvcP9szpckd6GHHc1qg44Rt70LTUqHTpY");
    }
}
