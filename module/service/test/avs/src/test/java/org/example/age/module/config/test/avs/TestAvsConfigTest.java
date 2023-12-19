package org.example.age.module.config.test.avs;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import javax.inject.Singleton;
import org.example.age.service.config.avs.AvsConfig;
import org.example.age.service.config.avs.RefreshableAvsConfigProvider;
import org.junit.jupiter.api.Test;

public final class TestAvsConfigTest {

    @Test
    public void get() {
        RefreshableAvsConfigProvider avsConfigProvider = TestComponent.createRefreshableAvsConfigProvider();
        AvsConfig avsConfig = avsConfigProvider.get();
        assertThat(avsConfig).isNotNull();
    }

    /** Dagger component that provides a {@link RefreshableAvsConfigProvider}. */
    @Component(modules = TestAvsConfigModule.class)
    @Singleton
    interface TestComponent {

        static RefreshableAvsConfigProvider createRefreshableAvsConfigProvider() {
            TestComponent component = DaggerTestAvsConfigTest_TestComponent.create();
            return component.refreshableAvsConfigProvider();
        }

        RefreshableAvsConfigProvider refreshableAvsConfigProvider();
    }
}
