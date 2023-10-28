package org.example.age.common.avs.store;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import javax.inject.Singleton;
import org.example.age.data.AgeThresholds;
import org.example.age.data.SecureId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class InMemorySiteConfigStoreTest {

    private SiteConfigStore siteConfigStore;

    @BeforeEach
    public void createSiteConfigStore() {
        siteConfigStore = TestComponent.createSiteConfigStore();
    }

    @Test
    public void saveAndLoad() {
        SiteConfig siteConfig = createSiteConfig();
        siteConfigStore.save(siteConfig);
        assertThat(siteConfigStore.tryLoad(siteConfig.siteId())).hasValue(siteConfig);
    }

    @Test
    public void delete() {
        SiteConfig siteConfig = createSiteConfig();
        siteConfigStore.save(siteConfig);
        assertThat(siteConfigStore.tryLoad(siteConfig.siteId())).isPresent();

        siteConfigStore.delete(siteConfig.siteId());
        assertThat(siteConfigStore.tryLoad(siteConfig.siteId())).isEmpty();
    }

    private static SiteConfig createSiteConfig() {
        return SiteConfig.of("Site", AgeThresholds.of(18), SecureId.generate());
    }

    /** Dagger component that provides a {@link SiteConfigStore}. */
    @Component(modules = InMemorySiteConfigStoreModule.class)
    @Singleton
    interface TestComponent {

        static SiteConfigStore createSiteConfigStore() {
            TestComponent component = DaggerInMemorySiteConfigStoreTest_TestComponent.create();
            return component.siteConfigStore();
        }

        SiteConfigStore siteConfigStore();
    }
}
