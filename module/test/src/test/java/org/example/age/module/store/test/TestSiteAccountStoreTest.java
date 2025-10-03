package org.example.age.module.store.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.client.WebStageTesting.await;

import dagger.Component;
import jakarta.inject.Singleton;
import java.util.Optional;
import java.util.function.Supplier;
import org.example.age.common.api.VerifiedUser;
import org.example.age.service.module.store.testing.SiteAccountStoreTestTemplate;
import org.example.age.site.spi.SiteVerificationStore;
import org.example.age.testing.api.TestModels;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public final class TestSiteAccountStoreTest extends SiteAccountStoreTestTemplate {

    private static final SiteVerificationStore store = TestComponent.create();

    @Test
    @Override
    public void pseudonymConflict() {
        VerifiedUser user = TestModels.createVerifiedUser();
        Optional<String> maybeConflictingAccountId = await(store().trySave("duplicate", user, expiration()));
        assertThat(maybeConflictingAccountId).isPresent();
    }

    @Disabled
    @Test
    @Override
    public void verified_ExpiredPseudonymConflict() {}

    @Disabled
    @Test
    @Override
    public void expired() {}

    @Override
    protected SiteVerificationStore store() {
        return store;
    }

    /** Dagger component for {@link SiteVerificationStore}. */
    @Component(modules = TestSiteAccountStoreModule.class)
    @Singleton
    interface TestComponent extends Supplier<SiteVerificationStore> {

        static SiteVerificationStore create() {
            return DaggerTestSiteAccountStoreTest_TestComponent.create().get();
        }
    }
}
