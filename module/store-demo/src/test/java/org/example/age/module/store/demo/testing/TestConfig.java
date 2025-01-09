package org.example.age.module.store.demo.testing;

import org.example.age.api.AgeRange;
import org.example.age.api.VerifiedUser;
import org.example.age.api.crypto.SecureId;
import org.example.age.module.store.demo.AvsStoresConfig;

/** Configuration for testing. */
public final class TestConfig {

    private static final AvsStoresConfig avsConfig = AvsStoresConfig.builder()
            .putVerifiedAccounts(
                    "person",
                    VerifiedUser.builder()
                            .pseudonym(SecureId.generate())
                            .ageRange(AgeRange.builder().min(18).build())
                            .build())
            .build();

    /** Gets the {@link AvsStoresConfig}. */
    public static AvsStoresConfig avs() {
        return avsConfig;
    }

    // static class
    private TestConfig() {}
}
