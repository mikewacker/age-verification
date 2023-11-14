package org.example.age.common.avs.store;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import javax.inject.Singleton;
import org.example.age.common.avs.config.RegisteredSiteConfig;
import org.example.age.common.avs.config.SiteLocation;
import org.example.age.data.AgeThresholds;
import org.example.age.data.crypto.SecureId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class InMemoryRegisteredSiteConfigStoreTest {

    private RegisteredSiteConfigStore siteConfigStore;

    @BeforeEach
    public void createSiteConfigStore() {
        siteConfigStore = TestComponent.createSiteConfigStore();
    }

    @Test
    public void saveAndLoad() {
        RegisteredSiteConfig siteConfig = createRegisteredSiteConfig();
        siteConfigStore.save(siteConfig);
        assertThat(siteConfigStore.tryLoad(siteConfig.siteId())).hasValue(siteConfig);
    }

    @Test
    public void delete() {
        RegisteredSiteConfig siteConfig = createRegisteredSiteConfig();
        siteConfigStore.save(siteConfig);
        assertThat(siteConfigStore.tryLoad(siteConfig.siteId())).isPresent();

        siteConfigStore.delete(siteConfig.siteId());
        assertThat(siteConfigStore.tryLoad(siteConfig.siteId())).isEmpty();
    }

    private static RegisteredSiteConfig createRegisteredSiteConfig() {
        SiteLocation siteLocation =
                SiteLocation.builder("localhost", 80).redirectPath("").build();
        return RegisteredSiteConfig.builder("Site")
                .siteLocation(siteLocation)
                .ageThresholds(AgeThresholds.of(18))
                .pseudonymKey(SecureId.generate())
                .build();
    }

    /** Dagger component that provides a {@link RegisteredSiteConfigStore}. */
    @Component(modules = InMemoryRegisteredSiteConfigStoreModule.class)
    @Singleton
    interface TestComponent {

        static RegisteredSiteConfigStore createSiteConfigStore() {
            TestComponent component = DaggerInMemoryRegisteredSiteConfigStoreTest_TestComponent.create();
            return component.siteConfigStore();
        }

        RegisteredSiteConfigStore siteConfigStore();
    }
}
