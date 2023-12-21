package org.example.age.module.config.resource;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import javax.inject.Singleton;
import org.example.age.module.internal.resource.TestResourceModule;
import org.example.age.service.config.AvsConfig;
import org.example.age.service.config.RefreshableAvsConfigProvider;
import org.junit.jupiter.api.Test;

final class ResourceAvsConfigTest {

    @Test
    public void get() {
        RefreshableAvsConfigProvider avsConfigProvider = TestComponent.createRefreshableAvsConfigProvider();
        AvsConfig avsConfig = avsConfigProvider.get();
        assertThat(avsConfig).isNotNull();
    }

    /** Dagger component that provides a {@link RefreshableAvsConfigProvider}. */
    @Component(modules = {ResourceAvsConfigModule.class, TestResourceModule.class})
    @Singleton
    interface TestComponent {

        static RefreshableAvsConfigProvider createRefreshableAvsConfigProvider() {
            TestComponent component = DaggerResourceAvsConfigTest_TestComponent.create();
            return component.refreshableAvsConfigProvider();
        }

        RefreshableAvsConfigProvider refreshableAvsConfigProvider();
    }
}
