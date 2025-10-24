package org.example.age.site.provider.userlocalizer.demo;

import org.example.age.testing.config.TestConfigParser;
import org.junit.jupiter.api.Test;

public final class SiteLocalizationKeyConfigTest {

    @Test
    public void parse() throws Exception {
        TestConfigParser.forClass(SiteLocalizationKeyConfig.class)
                .parseLines("key: NWKnDAiC7iM_hqothKM5Lnaor0xS77DzV9q9QpSeJLc");
    }
}
