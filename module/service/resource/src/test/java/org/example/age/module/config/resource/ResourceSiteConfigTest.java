package org.example.age.module.config.resource;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import javax.inject.Singleton;
import org.example.age.module.internal.resource.TestSiteResourceModule;
import org.example.age.service.config.RefreshableSiteConfigProvider;
import org.example.age.service.config.SiteConfig;
import org.junit.jupiter.api.Test;

public final class ResourceSiteConfigTest {

    @Test
    public void get() {
        RefreshableSiteConfigProvider siteConfigProvider = TestComponent.createRefreshableSiteConfigProvider();
        SiteConfig siteConfig = siteConfigProvider.get();
        assertThat(siteConfig).isNotNull();
    }

    /** Dagger component that provides a {@link RefreshableSiteConfigProvider}. */
    @Component(modules = {ResourceSiteConfigModule.class, TestSiteResourceModule.class})
    @Singleton
    interface TestComponent {

        static RefreshableSiteConfigProvider createRefreshableSiteConfigProvider() {
            TestComponent component = DaggerResourceSiteConfigTest_TestComponent.create();
            return component.refreshableSiteConfigProvider();
        }

        RefreshableSiteConfigProvider refreshableSiteConfigProvider();
    }
}
