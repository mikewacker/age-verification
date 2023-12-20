package org.example.age.module.config.test.site;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import javax.inject.Singleton;
import org.example.age.service.config.site.RefreshableSiteConfigProvider;
import org.example.age.service.config.site.SiteConfig;
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
