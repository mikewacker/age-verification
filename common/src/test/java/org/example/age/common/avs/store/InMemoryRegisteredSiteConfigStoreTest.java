package org.example.age.common.avs.store;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.net.HostAndPort;
import dagger.Component;
import javax.inject.Singleton;
import org.example.age.common.avs.config.RegisteredSiteConfig;
import org.example.age.data.AgeThresholds;
import org.example.age.data.SecureId;
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
        RegisteredSiteConfig siteConfig = createSiteConfig();
        siteConfigStore.save(siteConfig);
        assertThat(siteConfigStore.tryLoad(siteConfig.siteId())).hasValue(siteConfig);
    }

    @Test
    public void delete() {
        RegisteredSiteConfig siteConfig = createSiteConfig();
        siteConfigStore.save(siteConfig);
        assertThat(siteConfigStore.tryLoad(siteConfig.siteId())).isPresent();

        siteConfigStore.delete(siteConfig.siteId());
        assertThat(siteConfigStore.tryLoad(siteConfig.siteId())).isEmpty();
    }

    private static RegisteredSiteConfig createSiteConfig() {
        return RegisteredSiteConfig.builder("Site")
                .siteLocation(HostAndPort.fromParts("localhost", 80))
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
