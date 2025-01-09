package org.example.age.module.store.demo.testing;

import org.example.age.module.store.demo.AvsStoresConfig;
import org.example.age.testing.TestModels;

/** Configuration for testing. */
public final class TestConfig {

    private static final AvsStoresConfig avsConfig = AvsStoresConfig.builder()
            .putVerifiedAccounts("person", TestModels.createVerifiedUser())
            .build();

    /** Gets the {@link AvsStoresConfig}. */
    public static AvsStoresConfig avs() {
        return avsConfig;
    }

    // static class
    private TestConfig() {}
}
