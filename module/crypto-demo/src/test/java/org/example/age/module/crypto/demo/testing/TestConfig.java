package org.example.age.module.crypto.demo.testing;

import org.example.age.api.crypto.SecureId;
import org.example.age.module.crypto.demo.AvsKeysConfig;
import org.example.age.module.crypto.demo.SiteKeysConfig;

/** Configuration for testing. */
public final class TestConfig {

    private static final SiteKeysConfig siteConfig = SiteKeysConfig.builder()
            .localization(SecureId.generate())
            .signing(ConfigKeyPair.publicKey())
            .build();
    private static final AvsKeysConfig avsConfig = AvsKeysConfig.builder()
            .putLocalization("site", SecureId.generate())
            .signing(ConfigKeyPair.privateKey())
            .build();

    /** Gets the {@link SiteKeysConfig}. */
    public static SiteKeysConfig site() {
        return siteConfig;
    }

    /** Gets the {@link AvsKeysConfig}. */
    public static AvsKeysConfig avs() {
        return avsConfig;
    }

    // static class
    private TestConfig() {}
}
