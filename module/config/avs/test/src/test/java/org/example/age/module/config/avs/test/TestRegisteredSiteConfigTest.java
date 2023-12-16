package org.example.age.module.config.avs.test;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import java.util.Optional;
import javax.inject.Singleton;
import org.example.age.service.config.avs.RegisteredSiteConfig;
import org.example.age.service.module.config.RefreshableRegisteredSiteConfigProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TestRegisteredSiteConfigTest {

    private static RefreshableRegisteredSiteConfigProvider siteConfigProvider;

    @BeforeAll
    public static void createRefreshableRegisteredSiteConfigProvider() {
        siteConfigProvider = TestComponent.createRefreshableRegisteredSiteConfigProvider();
    }

    @Test
    public void tryGet_RegisteredSite() {
        Optional<RegisteredSiteConfig> maybeSiteConfig = siteConfigProvider.tryGet("Site");
        assertThat(maybeSiteConfig).isPresent();
    }

    @Test
    public void tryGet_UnregisteredSite() {
        Optional<RegisteredSiteConfig> maybeSiteConfig = siteConfigProvider.tryGet("DNE");
        assertThat(maybeSiteConfig).isEmpty();
    }

    /** Dagger component that provides a {@link RefreshableRegisteredSiteConfigProvider}. */
    @Component(modules = TestAvsConfigModule.class)
    @Singleton
    interface TestComponent {

        static RefreshableRegisteredSiteConfigProvider createRefreshableRegisteredSiteConfigProvider() {
            TestComponent component = DaggerTestRegisteredSiteConfigTest_TestComponent.create();
            return component.refreshableRegisteredSiteConfigProvider();
        }

        RefreshableRegisteredSiteConfigProvider refreshableRegisteredSiteConfigProvider();
    }
}
