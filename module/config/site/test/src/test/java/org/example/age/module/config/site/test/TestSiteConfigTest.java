package org.example.age.module.config.site.test;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import javax.inject.Singleton;
import org.example.age.service.config.site.SiteConfig;
import org.example.age.service.module.config.site.RefreshableSiteConfigProvider;
import org.junit.jupiter.api.Test;

public final class TestSiteConfigTest {

    @Test
    public void get() {
        RefreshableSiteConfigProvider siteConfigProvider = TestComponent.createRefreshableSiteConfigProvider();
        SiteConfig siteConfig = siteConfigProvider.get();
        assertThat(siteConfig).isNotNull();
    }

    /** Dagger component that provides a {@link RefreshableSiteConfigProvider}. */
    @Component(modules = TestSiteConfigModule.class)
    @Singleton
    interface TestComponent {

        static RefreshableSiteConfigProvider createRefreshableSiteConfigProvider() {
            TestComponent component = DaggerTestSiteConfigTest_TestComponent.create();
            return component.refreshableSiteConfigProvider();
        }

        RefreshableSiteConfigProvider refreshableSiteConfigProvider();
    }
}
