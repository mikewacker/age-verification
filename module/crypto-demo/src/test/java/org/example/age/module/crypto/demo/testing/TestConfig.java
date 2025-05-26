package org.example.age.module.crypto.demo.testing;

import org.example.age.api.crypto.SecureId;
import org.example.age.module.crypto.demo.keys.AvsKeysConfig;
import org.example.age.module.crypto.demo.keys.SiteKeysConfig;

/** Configuration for testing. */
public final class TestConfig {

    private static final SiteKeysConfig siteKeys = SiteKeysConfig.builder()
            .localization(SecureId.generate())
            .signing(ConfigKeyPair.publicKey())
            .build();
    private static final AvsKeysConfig avsKeys = AvsKeysConfig.builder()
            .putLocalization("site", SecureId.generate())
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
