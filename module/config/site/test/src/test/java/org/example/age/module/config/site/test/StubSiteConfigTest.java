package org.example.age.module.config.site.test;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import javax.inject.Provider;
import javax.inject.Singleton;
import org.example.age.module.config.site.SiteConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class StubSiteConfigTest {

    private static Provider<SiteConfig> siteConfigProvider;

    @BeforeAll
    public static void createSiteConfigProvider() {
        siteConfigProvider = TestComponent.createSiteConfigProvider();
    }

    @Test
    public void get() {
        SiteConfig siteConfig = siteConfigProvider.get();
        assertThat(siteConfig).isNotNull();
    }

    @Component(modules = StubSiteConfigModule.class)
    @Singleton
    interface TestComponent {

        static Provider<SiteConfig> createSiteConfigProvider() {
            TestComponent component = DaggerStubSiteConfigTest_TestComponent.create();
            return component.siteConfigProvider();
        }

        Provider<SiteConfig> siteConfigProvider();
    }
}
