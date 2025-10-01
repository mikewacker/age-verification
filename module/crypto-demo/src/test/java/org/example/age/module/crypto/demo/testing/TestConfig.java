package org.example.age.module.crypto.demo.testing;

import java.util.Map;
import org.example.age.common.api.crypto.SecureId;
import org.example.age.module.crypto.demo.keys.AvsKeysConfig;
import org.example.age.module.crypto.demo.keys.SiteKeysConfig;

/** Configuration for testing. */
public final class TestConfig {

    private static final SiteKeysConfig siteKeys = SiteKeysConfig.builder()
            .localization(SecureId.generate())
            .signing(ConfigKeyPair.publicKey())
            .build();
    private static final AvsKeysConfig avsKeys = AvsKeysConfig.builder()
            .putAllLocalization(Map.of("site1", SecureId.generate(), "site2", SecureId.generate()))
            .signing(ConfigKeyPair.privateKey())
            .build();

    /** Gets the configuration for keys on the site. */
    public static SiteKeysConfig siteKeys() {
        return siteKeys;
    }

    /** Gets the configuration for keys on the age verification service. */
    public static AvsKeysConfig avsKeys() {
        return avsKeys;
    }

    private TestConfig() {} // static class
}
